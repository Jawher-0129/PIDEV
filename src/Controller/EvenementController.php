<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\String\Slugger\SluggerInterface;
use App\Entity\Evenement;
use App\Form\EvenementType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use App\Repository\EvenementRepository;
use Doctrine\ORM\EntityManagerInterface;
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Knp\Component\Pager\PaginatorInterface;
use Flasher\Prime\FlasherInterface;



class EvenementController extends AbstractController
{
    #[Route('/evenement', name: 'app_evenement')]
    public function index(): Response
    {
        return $this->render('evenement/index.html.twig', [
            'controller_name' => 'EvenementController',
        ]);
    }
    //Ajout front
    #[Route('/evenement/add', name: 'add_evenement')]
    public function ajouterEvenement(ManagerRegistry $doctrine, Request $request, SluggerInterface $slugger): Response
    {
        $nouvelleEvenement = new Evenement();
    
        $form = $this->createForm(EvenementType::class, $nouvelleEvenement);
        $form->add('Enregistrer', SubmitType::class);
    
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager = $doctrine->getManager();
    
            // Handle potential image upload
            $imageFile = $form->get('Image')->getData(); // Access the 'image' field
    
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();
    
                try {
                    $imageFile->move(
                        $this->getParameter('images_directory'),
                        $newFilename
                    );
                } catch (FileException $e) {
                    // Handle exception if something happens during file upload
                }
    
                $nouvelleEvenement->setImage($newFilename);
            }
    
            $entityManager->persist($nouvelleEvenement);
            $entityManager->flush();
    
            // Redirect the user to the confirmation page or any other appropriate page
            return $this->redirectToRoute('all_evenements');
        }
    
        return $this->render('evenement/evenementform.html.twig', [
            'form' => $form->createView(),
        ]);
    }
        //Affichage
        #[Route('/show/evenement', name: 'all_evenements')]
        public function allEvenement(EvenementRepository $evenementRepository): Response
        {
            $evenements = $evenementRepository->findAll();
    
            return $this->render('evenement/evenements.html.twig', [
                'evenements' => $evenements,
            ]);
        }
        //modification 
        #[Route('/evenement/update/{id}', name: 'update_evenement')]
        public function updateEvenement($id, EvenementRepository $evenementRepository, EntityManagerInterface $entityManager, Request $request, SluggerInterface $slugger): Response
        {
            $evenement = $evenementRepository->find($id);
        
            // Check if the evenement exists
            if (!$evenement) {
                throw $this->createNotFoundException('The evenement does not exist');
            }
        
            // Make sure to store the existing image filename before creating the form
            $existingImageFilename = $evenement->getImage();
        
            $form = $this->createForm(EvenementType::class, $evenement);
            $form->add('Modifier', SubmitType::class);
        
            if ($request->isMethod('POST')) {
                $form->handleRequest($request);
        
                if ($form->isSubmitted() && $form->isValid()) {
                    // Handle potential image updates
                    $imageFile = $form->get('Image')->getData(); // Access the 'image' field
        
                    if ($imageFile) {
                        $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                        $safeFilename = $slugger->slug($originalFilename);
                        $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();
        
                        try {
                            $imageFile->move(
                                $this->getParameter('images_directory'),
                                $newFilename
                            );
                        } catch (FileException $e) {
                            // Handle exception if something happens during file upload
                        }
        
                        $evenement->setImage($newFilename);
                    } else {
                        // If no new image is provided, keep the existing image filename
                        $evenement->setImage($existingImageFilename);
                    }
        
                    $entityManager->flush();
        
                    $this->addFlash('success', 'Evenement updated successfully.');
        
                    return $this->redirectToRoute('all_evenements');
                } else {
                    $this->addFlash('error', 'Form submission failed. Please check the errors.');
                }
            }
        
            return $this->render('evenement/evenementform.html.twig', ['form' => $form->createView()]);
        }
        //Supression

        #[Route('/evenement/delete/{id}', name: 'delete_evenement')]
    public function deleteEvenement($id, EvenementRepository $evenementRepository, EntityManagerInterface $entityManager): Response
{
    $evenement = $evenementRepository->find($id);

    if (!$evenement) {
        throw $this->createNotFoundException('Evenement not found');
    }

    // Obtenez l'entité Actualite associée
    $actualite = $evenement->getActualite();

    // S'il y a une entité Actualite associée, supprimez-la d'abord
    if ($actualite) {
        $evenement->setActualite(null); // Dissociez l'Actualite de l'Evenement
        $entityManager->persist($evenement); // Persistez les changements
        $entityManager->flush(); // Appliquez les changements à la base de données

        // Supprimez l'entité Actualite
        $entityManager->remove($actualite);
        $entityManager->flush();
    }

    // Supprimez l'entité Evenement
    $entityManager->remove($evenement);
    $entityManager->flush();

    // Supprimer le fichier image associé s'il existe
    $imageFilename = $evenement->getImage();
    if ($imageFilename) {
        $imagePath = $this->getParameter('images_directory') . '/' . $imageFilename;

        if (file_exists($imagePath)) {
            unlink($imagePath);
        }
    }

    $this->addFlash('success', 'Evenement deleted successfully.');

    return $this->redirectToRoute('all_evenements');
}

        
        //show back 
        #[Route('/showEvenementsAdmin', name: 'showEvenementsAdmin')]
        public function showEvenementAdmin(EvenementRepository $repo): Response
        {
            $evenements = $repo->findAll();
            
            return $this->render('evenement/showEvenementAdmin.html.twig', [
                'evenements' => $evenements
            ]);
        }
    //delete back 
    #[Route('/deleteEvenement/{id}', name: 'delete_evenementba')]
    public function suppEvenement($id, EvenementRepository $evenementRepository, EntityManagerInterface $entityManager): Response
    {
        $evenement = $evenementRepository->find($id);
        
        if (!$evenement) {
            throw $this->createNotFoundException('The evenement does not exist');
        }
        $evenement->setActualite(null);
        $entityManager->remove($evenement);
        $entityManager->flush();
        
        $this->addFlash('notice', 'Evenement has been deleted successfully.');

        return $this->redirectToRoute('showEvenementsAdmin');
    }
    //edit back 
     
     #[Route('/editEvenement/{id}', name: 'edit_evenement')]
     public function editEvenement($id, EvenementRepository $evenementRepository, EntityManagerInterface $entityManager, Request $request, SluggerInterface $slugger): Response
     {
         $evenement = $evenementRepository->find($id);
     
         // Check if the evenement exists
         if (!$evenement) {
             throw $this->createNotFoundException('The evenement does not exist');
         }
     
         // Make sure to store the existing image filename before creating the form
         $existingImageFilename = $evenement->getImage();
     
         $form = $this->createForm(EvenementType::class, $evenement);
         $form->add('Modifier', SubmitType::class);
     
         if ($request->isMethod('POST')) {
             $form->handleRequest($request);
     
             if ($form->isSubmitted() && $form->isValid()) {
                 // Handle potential image updates
                 $imageFile = $form->get('Image')->getData(); // Access the 'image' field
     
                 if ($imageFile) {
                     $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                     $safeFilename = $slugger->slug($originalFilename);
                     $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();
     
                     try {
                         $imageFile->move(
                             $this->getParameter('images_directory'),
                             $newFilename
                         );
                     } catch (FileException $e) {
                         // Handle exception if something happens during file upload
                     }
     
                     $evenement->setImage($newFilename);
                 } else {
                     // If no new image is provided, keep the existing image filename
                     $evenement->setImage($existingImageFilename);
                 }
     
                 $entityManager->flush();
     
                 $this->addFlash('success', 'Evenement updated successfully.');
     
                 return $this->redirectToRoute('showEvenementsAdmin');
             } else {
                 $this->addFlash('error', 'Form submission failed. Please check the errors.');
             }
         }
     
         return $this->render('evenement/editEvenement.html.twig', ['form' => $form->createView()]);
     }
     //ajout back 
     #[Route('/addEvenement', name: 'add_evenementback')]
     public function addEvenement(ManagerRegistry $doctrine, Request $request, SluggerInterface $slugger, FlasherInterface $flasher,SecurityController $security): Response
     {
         $nouvelleEvenement = new Evenement();
     
         $form = $this->createForm(EvenementType::class, $nouvelleEvenement);
         $form->add('Enregistrer', SubmitType::class);
     
         $form->handleRequest($request);
     
         if ($form->isSubmitted() && $form->isValid()) {
             $entityManager = $doctrine->getManager();
             $user=$security->getUser();
             $nouvelleEvenement->setUser($user);
     
             // Handle potential image upload
             $imageFile = $form->get('Image')->getData(); // Access the 'image' field
     
             if ($imageFile) {
                 $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                 $safeFilename = $slugger->slug($originalFilename);
                 $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();
     
                 try {
                     $imageFile->move(
                         $this->getParameter('images_directory'),
                         $newFilename
                     );
                 } catch (FileException $e) {
                     // Handle exception if something happens during file upload
                 }
     
                 $nouvelleEvenement->setImage($newFilename);
             }
     
             $entityManager->persist($nouvelleEvenement);
             $entityManager->flush();
     
             // Add a success flash message
             $flasher->addSuccess('Événement ajouté avec succès.');
     
             // Redirect the user to the confirmation page or any other appropriate page
             return $this->redirectToRoute('showEvenementsAdmin');
         }
     
         return $this->render('evenement/addEvenement.html.twig', [
             'form' => $form->createView(),
         ]);
     }
    //searchbar
    #[Route('/evenement/search', name: 'search_evenements')]
    public function searchEvenements(Request $request, EvenementRepository $evenementRepository): Response
    {
        $query = $request->query->get('query');
        $evenements = [];

        if ($query) {
            $evenements = $evenementRepository->findByTitre($query);
        } else {
            $evenements = $evenementRepository->findAll();
        }

        return $this->render('evenement/evenements.html.twig', [
            'evenements' => $evenements,
            'query' => $query,
        ]);
    }
        //afiichage par id
        #[Route('/detailsEvenement/{id}' , name:'evenementdetails')]
        public function showEvenementDetails ($id, EvenementRepository $repo):Response
        {
            $evenement= $repo->find($id);
            return $this->render('evenement/EvenementDetails.html.twig',['evenement'=>$evenement]);
        }  
    //excel 
    #[Route('/evenement/export', name: 'export_evenements')]
    public function exportEvenements(EvenementRepository $evenementRepository): Response
    {
        $events = $evenementRepository->findAll();
        
        // Create new Spreadsheet object
        $spreadsheet = new Spreadsheet();

        // Get active sheet
        $sheet = $spreadsheet->getActiveSheet();

        // Add headers
        $sheet->setCellValue('A1', 'ID');
        $sheet->setCellValue('B1', 'Titre');
        $sheet->setCellValue('C1', 'Date');
        $sheet->setCellValue('D1', 'Duree');
        $sheet->setCellValue('E1', 'Lieu');
        $sheet->setCellValue('F1', 'Objectif');
        $sheet->setCellValue('G1', 'Image');

        // Add data
        $row = 2;
        foreach ($events as $event) {
            $sheet->setCellValue('A' . $row, $event->getId());
            $sheet->setCellValue('B' . $row, $event->getTitre());
            $sheet->setCellValue('C' . $row, $event->getDate()->format('Y-m-d'));
            $sheet->setCellValue('D' . $row, $event->getDuree());
            $sheet->setCellValue('E' . $row, $event->getLieu());
            $sheet->setCellValue('F' . $row, $event->getObjectif());
            $sheet->setCellValue('G' . $row, $event->getImage());
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
        $response->headers->set('Content-Disposition', 'attachment;filename="evenements.xlsx"');
        $response->headers->set('Cache-Control', 'max-age=0');

        // Clean up the temporary file
        unlink($tempFile);

        return $response;
    }  
    //trier par date 
    #[Route('/evenement/sort', name: 'sort_evenements')]
    public function sortEvenements(Request $request, EvenementRepository $evenementRepository): Response
    {
        $sortBy = $request->query->get('sort_by', 'date_asc');

        switch ($sortBy) {
            case 'date_asc':
                $evenements = $evenementRepository->findAllSortedByDateAsc();
                break;
            case 'date_desc':
                $evenements = $evenementRepository->findAllSortedByDateDesc();
                break;
            default:
                $evenements = $evenementRepository->findAll();
                break;
        }

        return $this->render('evenement/showEvenementAdmin.html.twig', [
            'evenements' => $evenements,
        ]);
    }

    


        
        

}