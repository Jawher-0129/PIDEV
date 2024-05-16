<?php

namespace App\Controller;

use App\Entity\Campagne;
use App\Form\CampagneType;
use App\Repository\CampagneRepository;
use App\Repository\DonRepository;
use App\Service\PdfService;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ManagerRegistry;
use Knp\Component\Pager\PaginatorInterface;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Security;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Form\Extension\Core\Type\SearchType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\String\Slugger\SluggerInterface;

class CampagneController extends AbstractController
{
 //stat

 #[Route('/stat', name: 'stat')]
public function index(CampagneRepository $campagneRepository, DonRepository $donRepository): Response
{
    $campagnes = $campagneRepository->findAll();
    
    $campaignNames = [];
    $donationCounts = [];
    
    foreach ($campagnes as $campaign) {
        $campaignNames[] = $campaign->getTitre();
        $donationCounts[] = $donRepository->getDonStatisticsByCampagne($campaign->getId());
    }

    // Récupération des données pour la charte globale des dons
    $DonationChartData = $donRepository->getDonationChartDataOverall();

    // Nouveau: Récupération des statistiques des campagnes par mois de début
    $CampaignsByStartMonth = $campagneRepository->findCampaignsByStartMonth();

    return $this->render('campagne/stat.html.twig', [
        'campaignNames' => $campaignNames,
        'donationCounts' => $donationCounts,
        'DonationChartData' => $DonationChartData,
        'CampaignsByStartMonth' => $CampaignsByStartMonth,
    ]);
}

 
   //affichage


 #[Route('/campagnes', name: 'all_campagnes')]
 public function allCampagnes(CampagneRepository $campagneRepository): Response
 {
     $campagnes = $campagneRepository->findAll();

     return $this->render('campagne/campagnes.html.twig', [
         'campagnes' => $campagnes,
     ]);
 }

 
//pagination
#[Route('/campagnes_back', name: 'all_campagnes_back')]
public function allCampagnes_back(Request $request, CampagneRepository $campagneRepository, PaginatorInterface $paginator): Response
{
    // Créer le formulaire de recherche
    $searchForm = $this->createFormBuilder(null)
        ->setMethod('GET')
        ->add('searchTerm', SearchType::class, [
            'required' => false,
            'attr' => ['placeholder' => 'Rechercher...'],
            'label' => false,
        ])
        ->getForm();

    $searchForm->handleRequest($request);

    // Déterminer si un terme de recherche a été soumis
    if ($searchForm->isSubmitted() && $searchForm->isValid()) {
        $searchData = $searchForm->getData();
        // Utiliser findBySearchTerm pour filtrer les campagnes
        $campagnesQuery = $campagneRepository->findBySearchTerm($searchData['searchTerm']);
    } else {
        // Sinon, utiliser findAll mais avec une Query pour la pagination
        $campagnesQuery = $campagneRepository->createQueryBuilder('c')->getQuery();
    }

    // Pagination des résultats
    $pagination = $paginator->paginate(
        $campagnesQuery, // Modifié pour utiliser la query
        $request->query->getInt('page', 1),
        5
    );

    // Rendre la vue avec les campagnes filtrées et le formulaire de recherche
    return $this->render('campagne/campagnes_back.html.twig', [
        'campagnes' => $pagination, // Renommer en 'pagination' pour plus de clarté
        'searchForm' => $searchForm->createView(), // Passer le formulaire à la vue
    ]);
}
 

//Ajout 
    #[Route('/campagne/add', name: 'add_campagne')]
    public function ajouterCampagne(ManagerRegistry $doctrine, Request $request, SluggerInterface $slugger,SecurityController $security): Response
    {
        if($security->isGranted('ROLE_DIRECTEUR'))
        {
        $nouvelleCampagne = new Campagne();
    
        $form = $this->createForm(CampagneType::class, $nouvelleCampagne);
        $form ->add('Enregistrer', SubmitType::class, [
            'attr' => [
                'class' => 'btn btn-primary', // Add your custom styling here
            ],
        ]);
    
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $user=$security->getUser();
            $nouvelleCampagne->setDirecteur($user);
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
    
                $nouvelleCampagne->setImage($newFilename);
            }
    
            $entityManager->persist($nouvelleCampagne);
            $entityManager->flush();
    
            // Redirect the user to the confirmation page or any other appropriate page
            return $this->redirectToRoute('all_campagnes');
        }
    
        return $this->render('campagne/campagneform.html.twig', [
            'form' => $form->createView(),
            'title' => 'Ajouter une campagne ',
        ]);
    }
    }
    

    #[Route('/campagne_back/add', name: 'add_campagne_back')]
    public function ajouterCampagne_back(ManagerRegistry $doctrine, Request $request, SluggerInterface $slugger,SecurityController $security): Response
    {
        $nouvelleCampagne = new Campagne();
    
        $form = $this->createForm(CampagneType::class, $nouvelleCampagne);
        $form ->add('Enregistrer', SubmitType::class, [
            'attr' => [
                'class' => 'btn btn-primary', // Add your custom styling here
            ],
        ]);
    
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $user=$security->getUser();
            $nouvelleCampagne->setDirecteur($user);
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
    
                $nouvelleCampagne->setImage($newFilename);
            }
    
            $entityManager->persist($nouvelleCampagne);
            $entityManager->flush();
    
            // Redirect the user to the confirmation page or any other appropriate page
            return $this->redirectToRoute('all_campagnes_back');
        }
    
        return $this->render('campagne/campagneform_back.html.twig', [
            'form' => $form->createView(),
            'title' => 'Ajouter une campagne ',
        ]);
    }
    

    //Methode de modification

    #[Route('/campagne/update/{id}', name: 'update_campagne')]
    public function updateCampagne($id, CampagneRepository $campagneRepository, EntityManagerInterface $entityManager, Request $request, SluggerInterface $slugger, SecurityController $security): Response
    {
        if($security->isGranted('ROLE_DIRECTEUR'))
        {
        $campagne = $campagneRepository->find($id);

        if (!$campagne) {
            $this->addFlash('error', 'Campagne not found.');
            return $this->redirectToRoute('all_campagnes');
        }

        // Vérifier si l'utilisateur actuel est le directeur qui a créé la campagne
        if (!$security->isGranted('ROLE_DIRECTEUR') || $campagne->getDirecteur() !== $security->getUser()) {
            $this->addFlash('error', 'You are not authorized to edit this campagne.');
            return $this->redirectToRoute('all_campagnes');
        }

        $existingImageFilename = $campagne->getImage();

        $form = $this->createForm(CampagneType::class, $campagne);
        $form->add('Modifier', SubmitType::class, ['attr' => ['class' => 'btn btn-primary']]);

        if ($request->isMethod('POST')) {
            $form->handleRequest($request);

            if ($form->isSubmitted() && $form->isValid()) {
                // Handle potential image updates
                $imageFile = $form->get('Image')->getData();

                if ($imageFile) {
                    $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                    $safeFilename = $slugger->slug($originalFilename);
                    $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                    try {
                        $imageFile->move($this->getParameter('images_directory'), $newFilename);
                    } catch (FileException $e) {
                        // Handle exception if something happens during file upload
                    }

                    $campagne->setImage($newFilename);
                } else {
                    // If no new image is provided, keep the existing image filename
                    $campagne->setImage($existingImageFilename);
                }

                $entityManager->flush();

                $this->addFlash('success', 'Campagne updated successfully.');

                return $this->redirectToRoute('all_campagnes');
            } else {
                $this->addFlash('error', 'Form submission failed. Please check the errors.');
            }
        }

        return $this->render('campagne/campagneform.html.twig', [
            'form' => $form->createView(),
            'title' => 'Modifier campagne',
        ]);
    }
    }

    #[Route('/campagne_back/update/{id}', name: 'updatecampagne_back')]
    public function updateCampagne_back($id, CampagneRepository $campagneRepository, EntityManagerInterface $entityManager, Request $request, SluggerInterface $slugger): Response
    {
        $campagne = $campagneRepository->find($id);
    
        // Make sure to store the existing image filename before creating the form
        $existingImageFilename = $campagne->getImage();
    
        $form = $this->createForm(CampagneType::class, $campagne);
        $form->add('Modifier', SubmitType::class, [
            'attr' => ['class' => 'btn btn-primary'], 
        ]);
    
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
    
                    $campagne->setImage($newFilename);
                } else {
                    // If no new image is provided, keep the existing image filename
                    $campagne->setImage($existingImageFilename);
                }
    
                $entityManager->flush();
    
                $this->addFlash('success', 'Campagne updated successfully.');
    
                return $this->redirectToRoute('all_campagnes_back');
            } else {
                $this->addFlash('error', 'Form submission failed. Please check the errors.');
            }
        }
    
        return $this->render('campagne/campagneform_back.html.twig', ['form' => $form->createView(),
        'title' => 'Modifier campagne ',]);
    }
//Supression
    #[Route('/campagne/delete/{id}', name: 'delete_campagne')]
    public function deleteCampagne($id, CampagneRepository $campagneRepository, EntityManagerInterface $entityManager,SecurityController $security): Response
    {
        if($security->isGranted('ROLE_DIRECTEUR'))
        {
        
        $campagne = $campagneRepository->find($id);

          // Vérifier si l'utilisateur actuel est le directeur qui a créé la campagne
          if (!$security->isGranted('ROLE_DIRECTEUR') || $campagne->getDirecteur() !== $security->getUser()) {
            $this->addFlash('error', 'You are not authorized to edit this campagne.');
            return $this->redirectToRoute('all_campagnes');
        }

        if (!$campagne) {
            throw $this->createNotFoundException('Campagne not found');
        }

        // Delete the associated image file if it exists
        $imageFilename = $campagne->getImage();
        if ($imageFilename) {
            $imagePath = $this->getParameter('images_directory') . '/' . $imageFilename;

            if (file_exists($imagePath)) {
                unlink($imagePath);
            }
        }

        $entityManager->remove($campagne);
        $entityManager->flush();

        $this->addFlash('success', 'Campagne deleted successfully.');

        return $this->redirectToRoute('all_campagnes');
    }
    }

    #[Route('/campagne_back/delete/{id}', name: 'delete_campagne_back')]
    public function deleteCampagne_back($id, CampagneRepository $campagneRepository, EntityManagerInterface $entityManager): Response
    {

        $campagne = $campagneRepository->find($id);

        if (!$campagne) {
            throw $this->createNotFoundException('Campagne not found');
        }

        // Delete the associated image file if it exists
        $imageFilename = $campagne->getImage();
        if ($imageFilename) {
            $imagePath = $this->getParameter('images_directory') . '/' . $imageFilename;

            if (file_exists($imagePath)) {
                unlink($imagePath);
            }
        }

        $entityManager->remove($campagne);
        $entityManager->flush();

        $this->addFlash('success', 'Campagne deleted successfully.');

        return $this->redirectToRoute('all_campagnes_back');
    }

    //Affichage par id
    #[Route('/campagne/{id}', name: 'show_campagne')]
    public function showCampagne($id, CampagneRepository $campagneRepository,SecurityController $security): Response
    {
        $campagne = $campagneRepository->find($id);

        if (!$campagne) {
            throw $this->createNotFoundException('Campagne not found');
        }

        return $this->render('campagne/show_campagne.html.twig', [
            'campagne' => $campagne,
        ]);
    }

    #[Route('/campagne_back/{id}', name: 'show_campagne_back')]
    public function showCampagne_back($id, CampagneRepository $campagneRepository): Response
    {
        $campagne = $campagneRepository->find($id);

        if (!$campagne) {
            throw $this->createNotFoundException('Campagne not found');
        }

        return $this->render('campagne/show_campagne_back.html.twig', [
            'campagne' => $campagne,
        ]);
    }
}