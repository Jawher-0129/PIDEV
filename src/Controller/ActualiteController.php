<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\HttpFoundation\Request;
use App\Entity\Actualite;
use App\Form\ActualiteType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use App\Repository\ActualiteRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\StreamedResponse;
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use App\Form\ImportActualiteType;
use PhpOffice\PhpSpreadsheet\IOFactory;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use PhpOffice\PhpSpreadsheet;
use Flasher\Prime\FlasherInterface;
use Knp\Component\Pager\PaginatorInterface;

class ActualiteController extends AbstractController
{
    #[Route('/actualite', name: 'app_actualite')]
    public function index(): Response
    {
        return $this->render('actualite/index.html.twig', [
            'controller_name' => 'ActualiteController',
        ]);
    }
    //add
    #[Route('/actualite/add', name: 'add_actualite')]
    public function ajouterActualite(ManagerRegistry $doctrine, Request $request): Response
    {
        $nouvelleActualite = new Actualite();
    
        $form = $this->createForm(ActualiteType::class, $nouvelleActualite);
        $form->add('Enregistrer', SubmitType::class);
    
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager = $doctrine->getManager();
    

    
            $entityManager->persist($nouvelleActualite);
            $entityManager->flush();
    
            // Redirect the user to the confirmation page or any other appropriate page
            return $this->redirectToRoute('all_actualites');
        }
    
        return $this->render('actualite/actualiteform.html.twig', [
            'formA' => $form->createView(),
        ]);
    }
    //Affichage
    #[Route('/show/actualite', name: 'all_actualites')]
    public function allActualite(ActualiteRepository $actualiteRepository): Response
    {
        $actualites = $actualiteRepository->findAll();
        
        return $this->render('actualite/actualites.html.twig', [
            'actualites' => $actualites,
        ]);
    }
            //modification 
            #[Route('/actualite/update/{id}', name: 'update_actualite')]
            public function updateActualite($id, ActualiteRepository $actualiteRepository, EntityManagerInterface $entityManager, Request $request): Response
            {
                $actualite = $actualiteRepository->find($id);
            
                // Check if the evenement exists
                if (!$actualite) {
                    throw $this->createNotFoundException('The actualite does not exist');
                }
            
                $form = $this->createForm(ActualiteType::class, $actualite);
                $form->add('Modifier', SubmitType::class);
            
                if ($request->isMethod('POST')) {
                    $form->handleRequest($request);
            
                    if ($form->isSubmitted() && $form->isValid()) {
           
                        $entityManager->flush();
            
                        $this->addFlash('success', 'Actualite updated successfully.');
            
                        return $this->redirectToRoute('all_actualites');
                    } else {
                        $this->addFlash('error', 'Form submission failed. Please check the errors.');
                    }
                }
            
                return $this->render('actualite/actualiteform.html.twig', ['formA' => $form->createView()]);
            }
            //Supression
        #[Route('/actualite/delete/{id}', name: 'delete_actualite')]
        public function deleteActualite($id, ActualiteRepository  $actualiteRepository, EntityManagerInterface $entityManager): Response
        {
            $actualite = $actualiteRepository->find($id);

            if (!$actualite) {
                throw $this->createNotFoundException('Actualite not found');
            }

            $entityManager->remove($actualite);
            $entityManager->flush();

            $this->addFlash('success', 'Actualite deleted successfully.');

            return $this->redirectToRoute('all_actualites');
        }
        //show back 
        #[Route('/showActualiteAdmin', name: 'showActualiteAdmin')]
        public function showActualiteAdmin(ActualiteRepository $repo, PaginatorInterface $paginator, Request $request): Response
        {
            $query = $repo->createQueryBuilder('a')
            ->getQuery();

            $pagination = $paginator->paginate(
                $query, // Query to paginate
                $request->query->getInt('page', 1), 
                3 
            );

            return $this->render('actualite/showActualiteAdmin.html.twig', [
            'pagination' => $pagination
            ]);
        }
        //edit back  
        #[Route('/editActualite/{id}', name: 'edit_actualiteba')]
        public function editActualite($id, ActualiteRepository $actualiteRepository, EntityManagerInterface $entityManager, Request $request): Response
        {
            $actualite = $actualiteRepository->find($id);
        
            // Check if the evenement exists
            if (!$actualite) {
                throw $this->createNotFoundException('The actualite does not exist');
            }
        
            $form = $this->createForm(ActualiteType::class, $actualite);
            $form->add('Modifier', SubmitType::class);
        
            if ($request->isMethod('POST')) {
                $form->handleRequest($request);
        
                if ($form->isSubmitted() && $form->isValid()) {
       
                    $entityManager->flush();
        
                    $this->addFlash('success', 'Actualite updated successfully.');
        
                    return $this->redirectToRoute('showActualiteAdmin');
                } else {
                    $this->addFlash('error', 'Form submission failed. Please check the errors.');
                }
            }
        
            return $this->render('actualite/editActualite.html.twig', ['formA' => $form->createView()]);
        }
        //delete
        #[Route('/deleteActualite/{id}', name: 'delete_actualiteba')]
        public function suppActualite($id, ActualiteRepository  $actualiteRepository, EntityManagerInterface $entityManager): Response
        {
            $actualite = $actualiteRepository->find($id);

    if (!$actualite) {
        throw $this->createNotFoundException('Actualite not found');
    }

    // Annuler la relation avec Evenement
    $evenement = $actualite->getEvenement();
    if ($evenement !== null) {
        $evenement->setActualite(null);
        $entityManager->persist($evenement);
        $entityManager->flush();
    }

    // Supprimer l'entité Actualite
    $entityManager->remove($actualite);
    $entityManager->flush();

    $this->addFlash('success', 'Actualite deleted successfully.');

    return $this->redirectToRoute('showActualiteAdmin');
        }
        //add back
    #[Route('/addActualite', name: 'add_actualiteba')]
    public function addActualite(ManagerRegistry $doctrine, Request $request, FlasherInterface $flasher,SecurityController $security): Response
{
    $nouvelleActualite = new Actualite();
    
    $form = $this->createForm(ActualiteType::class, $nouvelleActualite);
    $form->add('Enregistrer', SubmitType::class);
    
    $form->handleRequest($request);
    
    if ($form->isSubmitted() && $form->isValid()) {
        $entityManager = $doctrine->getManager();
        $user=$security->getUser();
        $nouvelleActualite->setUser($user);
        
        $entityManager->persist($nouvelleActualite);
        $entityManager->flush();
        
        // Add a success flash message
        $flasher->addSuccess('Actualité ajoutée avec succès.');
        
        // Redirect the user to the confirmation page or any other appropriate page
        return $this->redirectToRoute('showActualiteAdmin');
    }
    
    return $this->render('actualite/addactualite.html.twig', [
        'formA' => $form->createView(),
    ]);
}
            //afiichage par id
            #[Route('/detailsActualite/{id}' , name:'actualitedetails')]
            public function showActualiteDetails ($id, ActualiteRepository $repo):Response
            {
                $actualite= $repo->find($id);
                return $this->render('actualite/stDetails.html.twig',['actualite'=>$actualite]);
            }
    // export excel 
    #[Route('/actualite/export', name: 'export_actualites')]
    public function exportActualites(ActualiteRepository $actualiteRepository): Response
    {
        $actualites = $actualiteRepository->findAll();
        
        // Create new Spreadsheet object
        $spreadsheet = new Spreadsheet();
    
        // Get active sheet
        $sheet = $spreadsheet->getActiveSheet();
    
        // Add headers
        $sheet->setCellValue('A1', 'ID');
        $sheet->setCellValue('B1', 'Titre');
        $sheet->setCellValue('C1', 'Description');
        $sheet->setCellValue('D1', 'Type Pub Cible');
        $sheet->setCellValue('E1', 'Theme');
    
        // Add data
        $row = 2;
        foreach ($actualites as $actualite) {
            $sheet->setCellValue('A' . $row, $actualite->getId());
            $sheet->setCellValue('B' . $row, $actualite->getTitre());
            $sheet->setCellValue('C' . $row, $actualite->getDescription());
            $sheet->setCellValue('D' . $row, $actualite->getTypePubCible());
            $sheet->setCellValue('E' . $row, $actualite->getTheme());
            $row++;
        }
    
        // Create Excel writer object
        $writer = new Xlsx($spreadsheet);
    
        // Create a temporary file to save the Excel file
        $tempFile = tempnam(sys_get_temp_dir(), 'excel');
    
        // Save Excel file to the temporary file
        $writer->save($tempFile);
    
        // Create a response object with the Excel file
        $response = new Response(file_get_contents($tempFile));
    
        // Set headers to force download the file
        $response->headers->set('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        $response->headers->set('Content-Disposition', 'attachment;filename="actualites.xlsx"');
        $response->headers->set('Cache-Control', 'max-age=0');
    
        // Clean up the temporary file
        unlink($tempFile);
    
        return $response;
    }
    //upload excel 
    #[Route('/upload-excel', name: 'xlsx')]
    public function xslx(Request $request, EntityManagerInterface $entityManager)
    {
        $file = $request->files->get('file');
        $fileFolder = __DIR__ . '/../../public/uploads/';
        $filePathName = md5(uniqid()) . $file->getClientOriginalName();

        try {
            $file->move($fileFolder, $filePathName);
        } catch (FileException $e) {
            dd($e);
        }

        $spreadsheet = IOFactory::load($fileFolder . $filePathName);
        $row = $spreadsheet->getActiveSheet()->removeRow(1);
        $sheetData = $spreadsheet->getActiveSheet()->toArray(null, true, true, true);

        foreach ($sheetData as $Row) {
            // Assuming the columns are A, B, C, D in your Excel file
            $titre = $Row['A']; // Titre
            $description = $Row['B']; // Description
            $typePubCible = $Row['C']; // Type Pub Cible
            $theme = $Row['D']; // Theme

            // You need to adjust the Actualite entity fields and setters based on your actual entity structure
            $actualite = new Actualite();
            $actualite->setTitre($titre);
            $actualite->setDescription($description);
            $actualite->setTypePubCible($typePubCible);
            $actualite->setTheme($theme);

            $entityManager->persist($actualite);
        }

        $entityManager->flush();
        
        // You can add a flash message here if you want to inform the user about the successful import
        $this->addFlash('success', 'Actualites imported successfully');

        // Redirect the user to a specific route or page
        return $this->redirectToRoute('showActualiteAdmin');
    }
        
}