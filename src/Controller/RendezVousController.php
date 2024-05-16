<?php

namespace App\Controller;

use App\Entity\Demande;
use App\Entity\RendezVous;
use App\Form\RendezVousType;
use App\Repository\DemandeRepository;
use App\Repository\RendezVousRepository;
use DateTime;
use Doctrine\ORM\EntityManagerInterface;
use MercurySeries\FlashyBundle\FlashyNotifier;
use PhpOffice\PhpSpreadsheet\IOFactory;
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use Symfony\Component\Security\Core\Security;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
 use Symfony\Component\HttpFoundation\Request; 
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Finder\Exception\AccessDeniedException;

#[Route('/rendezvous')]
class RendezVousController extends AbstractController
{
    

    #[Route('/sms/{id}', name: 'app_sms')]
public function sms(int $id, RendezVousRepository $rendezVousrepository): Response
{
    // Récupérer le numéro de téléphone du gamer à partir de la requête
    $DirecteurCampagneNumber = '+21628749555';
    
    // Récupérer la promotion depuis le repository
    $rd = $rendezVousrepository->find($id);
    $demande = $rd->getDemande();
     $directeurCampagne = $demande->getDirecteurCampagne()->getNom();
     
    
    
    // Remplacer les valeurs par les identifiants d'authentification Twilio ou Nexmo
    $sid = 'AC8bd6a0a5b22855c243dc60296543bf84';
    $token = 'c49c523215ae420d2cc398261c5a1b1b';
    $fromNumber = '+19149086373';
    $toNumber = $DirecteurCampagneNumber;
    
    // Instancier un objet client Twilio ou Nexmo
    $Client = new \Twilio\Rest\Client($sid, $token); // ou new \Nexmo\Client(new \Nexmo\Client\Credentials\Basic($sid, $token));
    
    // Construire le contenu du SMS
    $smsBody = "Voici la date de votre Rendez-vous : " . $rd->getdate()->format('d/m/Y H:i');
    $smsBody = "Voici la date de votre Rendez-vous (ID demande : " . $demande->getId_demande() . ") : " . $rd->getdate()->format('d/m/Y H:i') . " Directeur de campagne : " . $directeurCampagne;

    // Envoyer un SMS
    $Client->messages->create(
        $toNumber,
        [
            'from' => $fromNumber,
            'body' => $smsBody
        ]
    );
    
   // return new Response('SMS sent successfully!');
    return $this->redirectToRoute('app_rendez_vous_indexback');
}









    #[Route('/back', name: 'app_rendez_vous_indexback', methods: ['GET'])]
    public function indexback(RendezVousRepository $rendezVousRepository,): Response
    {
        return $this->render('rendez_vous/indexback.html.twig', [
            'rendez_vouses' => $rendezVousRepository->findAll(),
        ]);
    }
 ///Exportation__Excell
    #[Route('/excel', name: 'excel')]
    public function generateExcel(RendezVousRepository $rendezVousRepository, DemandeRepository $coursRepository): Response
    {
        
        
        // Récupérer tous les rendez-vous depuis le repository
        $rendezvousList = $rendezVousRepository->findAll();

        // Créer un objet Spreadsheet
        $spreadsheet = new Spreadsheet();

        // Créer la feuille de calcul
        $sheet = $spreadsheet->getActiveSheet();
        $sheet->setCellValue('A1', 'ID');
        $sheet->setCellValue('B1', 'Date');
        $sheet->setCellValue('C1', 'Lieu');
        $sheet->setCellValue('D1', 'Objectif');
        $sheet->setCellValue('E1', 'Demande');

        // Remplir la feuille de calcul avec les données
        $row = 2;
        foreach ($rendezvousList as $rendezvous) {
            $sheet->setCellValue('A' . $row, $rendezvous->getId_rendezvous());
            $sheet->setCellValue('B' . $row, $rendezvous->getDate()->format('Y-m-d H:i:s'));
            $sheet->setCellValue('C' . $row, $rendezvous->getLieu());
            $sheet->setCellValue('D' . $row, $rendezvous->getObjective());
            $sheet->setCellValue('E' . $row, $rendezvous->getDemande()->getId_demande());

            $row++;
        }

        // Enregistrer le fichier Excel temporaire
        $filename = 'rendezvous_export.xlsx';
        $excelFile = sys_get_temp_dir() . '/' . $filename;
        $writer = new Xlsx($spreadsheet);
        $writer->save($excelFile);

        return $this->file($excelFile, 'export.xlsx');
    }
//Imporation Execel

#[Route('/import/excel', name: 'rendezvous_import_excel')]
public function importFromExcel(Request $request, EntityManagerInterface $entityManager):Response
{
    // Vérifiez si un fichier a été soumis via un formulaire
    $uploadedFile = $request->files->get('excel_file');

    if (!$uploadedFile) {
        // Gérez le cas où aucun fichier n'a été soumis
        // Vous pouvez rediriger ou afficher un message d'erreur
    }

    // Chargez le fichier Excel
    $spreadsheet = IOFactory::load($uploadedFile);

    // Récupérez la première feuille de calcul
    $sheet = $spreadsheet->getActiveSheet();

    // Commencez à partir de la ligne 2 pour ignorer l'en-tête
    foreach ($sheet->getRowIterator(2) as $row) {
        // Obtenez les valeurs de chaque colonne
        $date = $sheet->getCell('B' . $row->getRowIndex())->getValue();
        $lieu = $sheet->getCell('C' . $row->getRowIndex())->getValue();
        $objective = $sheet->getCell('D' . $row->getRowIndex())->getValue();
        $demandeId = $sheet->getCell('E' . $row->getRowIndex())->getValue();

        $dateTime = \DateTime::createFromFormat('Y-m-d H:i:s', $date);

    if ($dateTime === false) {
        // Gérer l'erreur, peut-être enregistrer un message de journalisation
        continue; // Passez à la prochaine itération
    }

        // Créez une nouvelle entité RendezVous et persistez-la en base de données
        $rendezVous = new RendezVous();
        $rendezVous->setDate($dateTime);
        $rendezVous->setLieu($lieu);
        $rendezVous->setObjective($objective);

        // Vous devrez probablement récupérer l'entité Demande correspondant à l'ID dans votre cas
         $demande = $entityManager->getRepository(Demande::class)->find($demandeId);
         if ($demande !== null) {
            // Si la demande est trouvée, mettez à jour son statut
            $demande->setStatut('DEMANDE TRAITEE');
    
            // Créez une nouvelle entité RendezVous et persistez-la en base de données
            $rendezVous = new RendezVous();
            $rendezVous->setDate($dateTime);
            $rendezVous->setLieu($lieu);
            $rendezVous->setObjective($objective);
            $rendezVous->setDemande($demande);
            $entityManager->persist($rendezVous);
        } else {
            // Gérer le cas où la demande n'est pas trouvée
            // Vous pouvez enregistrer un message de journalisation ou gérer l'erreur d'une autre manière
        }
      /*    $demande->setStatut('DEMENDE TRAITEE');
        $rendezVous->setDemande($demande);
        $entityManager->persist($rendezVous); */
    }

    $entityManager->flush();
    return $this->redirectToRoute('app_rendez_vous_indexback');

    // Gérez le succès de l'importation
    // Vous pouvez rediriger ou afficher un message de succès

    // ...
}





    #[Route('/', name: 'app_rendez_vous_index', methods: ['GET'])]
    public function index(RendezVousRepository $rendezVousRepository, Security $security): Response
    {

        $user = $security->getUser();
    if (!$user) {
        throw new AccessDeniedException('Utilisateur non connecté.');
    }

        $rendez_vouses = $rendezVousRepository->findRendezVousByUtilisateur($user);
        return $this->render('rendez_vous/index.html.twig',[
            'rendez_vouses' => $rendez_vouses,
        ]);
    }

    #[Route('/new{demande}', name: 'app_rendez_vous_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager,Demande $demande,SecurityController $security,FlashyNotifier $flashy): Response
    {
        
         // Vérifier si la demande a déjà un rendez-vous
    $existingRendezVous = $entityManager->getRepository(RendezVous::class)->findOneBy(['demande' => $demande]);
    if ($demande->getStatut() === 'demande refuse') {
        //$this->addFlash('warning', 'La demande a été refusée, aucun rendez-vous ne peut être ajouté.');
        $flashy->error('Warning : La demande a été refusée, aucun rendez-vous ne peut être ajouté. !');
        return $this->redirectToRoute('app_demande_indexback');
    }

    // Si un rendez-vous existe déjà, vous pouvez afficher une alerte ou rediriger vers une autre page
    if ($existingRendezVous !== null) {
        //$this->addFlash('warning', 'Un rendez-vous existe déjà pour cette demande.');
        $flashy->error('Warning : Un rendez-vous est déjà affecté. !');

        return $this->redirectToRoute('app_demande_indexback');
    }
        $rendezVou = new RendezVous();
        $rendezVou->setDemande($demande);
        $form = $this->createForm(RendezVousType::class, $rendezVou);
        $form->handleRequest($request);
        $id = $rendezVou->getId_rendezvous();
        if ($form->isSubmitted() && $form->isValid()) {
            $demande->setStatut('DEMENDE TRAITEE');
            $rendezVou->getDemande()->setRendezVous($rendezVou);
            $entityManager->persist($rendezVou);
            $entityManager->flush();
            $flashy->success('Ajout Rendez-vous: Rendez-vous affectée !');


            return $this->redirectToRoute('app_rendez_vous_indexback', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('rendez_vous/new.html.twig', [
            'rendez_vou' => $rendezVou,
            'form' => $form,
        ]);
    }

    #[Route('/{id_rendezvous}', name: 'app_rendez_vous_show', methods: ['GET'])]
    public function show(RendezVous $rendezVou): Response
    {
        return $this->render('rendez_vous/show.html.twig', [
            'rendez_vou' => $rendezVou,
        ]);
    }

    #[Route('/{id_rendezvous}/edit', name: 'app_rendez_vous_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, RendezVous $rendezVou, EntityManagerInterface $entityManager,SecurityController $security): Response
    {
        $form = $this->createForm(RendezVousType::class, $rendezVou);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()){
            $entityManager->flush();
            return $this->redirectToRoute('app_rendez_vous_indexback', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('rendez_vous/edit.html.twig', [
            'rendez_vou' => $rendezVou,
            'form' => $form,
        ]);
    }

    #[Route('/{id_rendezvous}', name: 'app_rendez_vous_delete', methods: ['POST'])]
    public function delete(Request $request, RendezVous $rendezVou, EntityManagerInterface $entityManager,SecurityController $security): Response
    {
        if($security->isGranted('ROLE_DIRECTEUR'))
        {
        if ($this->isCsrfTokenValid('delete'.$rendezVou->getId_rendezvous(), $request->request->get('_token'))) {
            $entityManager->remove($rendezVou);
            $entityManager->flush();
        }
        return $this->redirectToRoute('app_rendez_vous_index', [], Response::HTTP_SEE_OTHER);

    }
    }
    #[Route('/delete/{id}', name: 'delete')]
    public function deleteR($id , RendezVousRepository $repo , EntityManagerInterface $em,SecurityController $security ): Response
    { 
        if($security->isGranted('ROLE_ADMIN'))
        {
        $r = $repo->find($id);
        $r->getDemande()->setRendezVous(null);

        $em->remove($r);
        $em->flush();
        return $this->redirectToRoute('app_rendez_vous_indexback');
        }
     } 
    
 
 






}
