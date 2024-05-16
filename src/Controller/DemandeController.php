<?php

namespace App\Controller;

use App\Entity\Demande;
use App\Form\DemandeType;
use App\Form\SearchType;
use App\Repository\DemandeRepository;
use App\Repository\DonRepository;
use App\Repository\RendezVousRepository;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ManagerRegistry;
use MercurySeries\FlashyBundle\FlashyNotifier;
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Finder\Exception\AccessDeniedException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Core\Security;

#[Route('/demande')]
class DemandeController extends AbstractController
{

    #[Route('/excel_demande', name: 'excel_demande')]
    public function generateExcel(DemandeRepository $demandeRepository, RendezVousRepository $rendezVousRepository): Response
    {
        // Récupérer tous les demandes depuis le repository
        $demandeList = $demandeRepository->findAll();

        // Créer un objet Spreadsheet
        $spreadsheet = new Spreadsheet();

        // Créer la feuille de calcul
        $sheet = $spreadsheet->getActiveSheet();
        $sheet->setCellValue('A1', 'ID');
        $sheet->setCellValue('B1', 'Date Demande');
        $sheet->setCellValue('C1', 'Description Demande');
        $sheet->setCellValue('D1', 'Statut Demande');
        $sheet->setCellValue('E1', 'Titre Demande');
        $sheet->setCellValue('F1', 'Date Rendez-vous');
        $sheet->setCellValue('G1', 'Lieu Rendez-vous');
        $sheet->setCellValue('H1', 'Objective Rendez-vous');

        // Remplir la feuille de calcul avec les données
        $row = 2;
        foreach ($demandeList as $demande) {
            $sheet->setCellValue('A' . $row, $demande->getId_demande());
            $sheet->setCellValue('B' . $row, $demande->getDate()->format('Y-m-d H:i:s'));
            $sheet->setCellValue('C' . $row, $demande->getDescription());
            $sheet->setCellValue('D' . $row, $demande->getStatut());
            $sheet->setCellValue('E' . $row, $demande->getTitre());

            // Si le statut est "DEMANDE TRAITE", récupérer les informations du rendez-vous
            if ($demande->getStatut() === 'DEMENDE TRAITEE') {
                $rendezVous = $rendezVousRepository->findOneBy(['demande' => $demande]);
                $this->setRendezVousInfo($sheet, $row, $rendezVous);
            }

            // Formater la cellule en fonction du statut
            $color = '#FFFFFF'; // Couleur par défaut
            if ($demande->getStatut() === 'EN COUR DE TRAITEMENT') {
                $color = 'FFFF00'; // Jaune
            } elseif ($demande->getStatut() === 'demande refuse') {
                $color = 'FF0000'; // Rouge
            } else {
                $color = '00FF00';
            }

            // Appliquer le style à la cellule
            $style = [
                'fill' => [
                    'fillType' => \PhpOffice\PhpSpreadsheet\Style\Fill::FILL_SOLID,
                    'startColor' => ['argb' => $color],
                ],
            ];

            $sheet->getStyle('D' . $row)->applyFromArray($style);

            $row++;
        }

        // Ajuster automatiquement la largeur des colonnes en fonction du contenu
        foreach (range('A', $sheet->getHighestDataColumn()) as $column) {
            $sheet->getColumnDimension($column)->setAutoSize(true);
        }

        // Enregistrer le fichier Excel temporaire
        $filename = 'demande_export.xlsx';
        $excelFile = sys_get_temp_dir() . '/' . $filename;
        $writer = new Xlsx($spreadsheet);
        $writer->save($excelFile);

        return $this->file($excelFile, 'demande_export.xlsx');
    }

    #[Route('/{id_demande}', name: 'app_demande_delete', methods: ['POST'])]
    public function delete(Request $request, Demande $demande, EntityManagerInterface $entityManager,FlashyNotifier $flashy): Response
    {
        if ($this->isCsrfTokenValid('delete'.$demande->getId_demande(), $request->request->get('_token'))) {
            $entityManager->remove($demande);
            $entityManager->flush();
            $flashy->success('Supprission Demande : Demande supprimée!');
        }

        return $this->redirectToRoute('app_demande_index', [], Response::HTTP_SEE_OTHER);
    }


    // Fonction pour mettre en forme les informations du rendez-vous dans la feuille de calcul
    private function setRendezVousInfo($sheet, $row, $rendezVous): void
    {
        $sheet->setCellValue('F' . $row, $rendezVous->getDate()->format('Y-m-d H:i:s'));
        $sheet->setCellValue('G' . $row, $rendezVous->getLieu());
        $sheet->setCellValue('H' . $row, $rendezVous->getObjective());
    }

    #[Route('/', name: 'app_demande_index', methods: ['GET'])]
    public function index(DemandeRepository $demandeRepository, Security $security,FlashyNotifier $flashy,Request $request): Response
    {
        $user = $security->getUser();
        if (!$user) {
            throw new AccessDeniedException('Utilisateur non connecté.');
        }
        $searchTerm = $request->query->get('searchTerm');
        $demandes = $demandeRepository->findDemandesByUtilisateur($user);
        if ($searchTerm) {
            $demandes = $demandeRepository->searchByTerm($searchTerm);
        }
    
        return $this->render('demande/index.html.twig', [
            'searchTerm' => $searchTerm,
            'demandes' => $demandes,
        ]);
    }
    #[Route('/back', name: 'app_demande_indexback')]
    public function demandeStatistics(DemandeRepository $demandeRepository,Request $request ): Response
    {
        $searchTerm = $request->query->get('searchTerm');
        $demandes = $demandeRepository->findAll();
        if ($searchTerm) {
            $demandes = $demandeRepository->searchByTerm($searchTerm);
        }

        // Initialize an empty array to store the statistics
        $statistics = [];
    
        // Count the number of demandes in each statut category
        foreach ($demandes as $demande) {
            $Statut = $demande->getStatut();
    
            if (!isset($statistics[$Statut])) {
                $statistics[$Statut] = 1;
            } else {
                $statistics[$Statut]++;
            }
        }
    
        // Render the statistics in a template
        return $this->render('demande/demande_statistics.html.twig', [
            'statistics' => $statistics,
            'demandes' => $demandes,
            'searchTerm' => $searchTerm,

        ]);
    }

    #[Route('/searchRef', name: 'searchid')]
    public function searchRef( DemandeRepository $demandeRepository ,Request $request ): Response
    { 
        $demandes = $demandeRepository->findAll();
    $form = $this->createForm(SearchType::class);
    $form->handleRequest($request);

    $resultats = [];

    if ($form->isSubmitted() && $form->isValid()) {
        $critere = $form->get('critere')->getData();

        // Divisez la valeur du critère en ID et Titre
        $criteria = [
            'id_demande' => is_numeric($critere) ? (int)$critere : null,
            'titre' => is_numeric($critere) ? null : $critere,
        ];

        // Appelez la méthode findByCriteria du repository pour récupérer les résultats
        $demandes = $demandeRepository->findByCriteria($criteria);
    }

    return $this->render('demande/ListAndSearch.html.twig', [
        'demandes' => $demandes,
        'form' => $form->createView(),
       
    ]);
    } 

    /*  #[Route('/back', name: 'app_demande_indexback', methods: ['GET'])]
    public function indexback(DemandeRepository $demandeRepository): Response
    {
        return $this->render('demande/indexback.html.twig', [
            'demandes' => $demandeRepository->findAll(),
        ]);
    }  */
   

    #[Route('/new/{id}', name: 'app_demande_new', methods: ['GET', 'POST'])]
    public function new($id, Request $request, EntityManagerInterface $entityManager, DonRepository $rep,SecurityController $security,FlashyNotifier $flashy
    ): Response
    {
        if($security->isGranted('ROLE_DIRECTEUR'))
        {
        $demande = new Demande();
        $form = $this->createForm(DemandeType::class, $demande);
        $form->handleRequest($request);
        
        // Use find instead of findBy to get a single Don entity by its ID
        $don = $rep->find($id); 
        
        if ($form->isSubmitted() && $form->isValid()) {
            $user = $this->getUser();
            
            // Assuming setDirecteurCampagne sets the user making the demande,
            // and the user has a relation with the Demande entity.
            $demande->setDirecteurCampagne($user);
            
            // Ensure $don is not null before setting it to avoid foreign key constraint violation
            if ($don !== null) {
                $demande->setDon($don);
            } else {
                // Handle the case where $don is null if necessary
                // e.g., return an error message or redirect to a different page
            }
            
            $entityManager->persist($demande);
            $entityManager->flush();
            $flashy->info('Ajout Demande : Ajout avec succès !');
    
            return $this->redirectToRoute('app_demande_index', [], Response::HTTP_SEE_OTHER);
        }
    
        return $this->renderForm('demande/new.html.twig', [
            'demande' => $demande,
            'form' => $form,
        ]);
    }
    }

    #[Route('/{id_demande}', name: 'app_demande_show', methods: ['GET'])]
    public function show(Demande $demande): Response
    {
        return $this->render('demande/show.html.twig', [
            'demande' => $demande,
        ]);
    }

    #[Route('/{id_demande}/edit', name: 'app_demande_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Demande $demande, EntityManagerInterface $entityManager,SecurityController $security,FlashyNotifier $flashy): Response
    {
        if($security->isGranted('ROLE_DIRECTEUR'))
        {
        $form = $this->createForm(DemandeType::class, $demande);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            $flashy->info('Modification Demande: Demande modifié !');

            return $this->redirectToRoute('app_demande_index', [], Response::HTTP_SEE_OTHER);
        }
        return $this->renderForm('demande/edit.html.twig', [
            'demande' => $demande,
            'form' => $form,
        ]);
    }
    }

    
    #[Route('refus/{id_demande}', name: 'refuseDemende')]
    public function refuserDemande($id_demande, ManagerRegistry $doctrine,FlashyNotifier $flashy): Response
    {
        $repo= $doctrine->getRepository(Demande::class);
        $demande = $repo->find($id_demande);
        if ($demande->getStatut() === 'DEMENDE TRAITEE') {
            //$this->addFlash('warning', 'La demande a été refusée, aucun rendez-vous ne peut être ajouté.');
            $flashy->error('Warning : Un rendez-vous est déjà affecté. !');
            return $this->redirectToRoute('app_demande_indexback');
        }
        $demande->setStatut('demande refuse');
        $em= $doctrine->getManager();
        $em->flush();
        $flashy->success('Refus Demande : Demande refusée!'); 

      
        return $this->redirectToRoute('app_demande_indexback');
    }

    

    
   

    
}
