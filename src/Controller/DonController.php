<?php

namespace App\Controller;

use App\Entity\Don;
use App\Entity\User1;
use App\Form\DonType;
use App\Repository\CampagneRepository;
use App\Repository\DonRepository;
use App\Repository\User1Repository;
use App\Service\MailjetService;
use App\Service\PdfService;
use Doctrine\DBAL\Types\TextType;
use Doctrine\ORM\EntityManagerInterface;
use Knp\Component\Pager\PaginatorInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Form\Extension\Core\Type\SearchType;
use Symfony\Component\HttpFoundation\BinaryFileResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;
use Symfony\Component\Security\Core\Security;

#[Route('/don')]
class DonController extends AbstractController
{

    //fonction recherche 

     
  

    //pdf
    #[Route('/campagnes/{campagneId}/dons', name: 'app_don_list')]
    public function listeDonsParCampagne(DonRepository $donRepository, $campagneId, PdfService $pdf)
    {
        $dons = $donRepository->findDonsByCampagne($campagneId);

        // Vérifier si aucun don n'existe pour la campagne spécifiée
        if (empty($dons)) {
        
             //Option 2: Générer un PDF avec un message indiquant l'absence de dons
             $html = $this->renderView('don/no_don.html.twig');
             $pdfContent = $pdf->generateBinaryPDF($html);
             $response = new Response($pdfContent);
             $response->headers->set('Content-Type', 'application/pdf');
             $response->headers->set('Content-Disposition', 'attachment;filename=no_don_list.pdf');
             return $response;
        }

        $html = $this->render('don/don_list.html.twig', ['dons' => $dons])->getContent();
        $pdfContent = $pdf->generateBinaryPDF($html);

        $response = new Response($pdfContent);
        $response->headers->set('Content-Type', 'application/pdf');
        $response->headers->set('Content-Disposition', 'attachment;filename=don_list.pdf');

        return $response;
    }

     //Affichage
     /*#[Route('/', name: 'app_don_index', methods: ['GET'])]
     public function index(DonRepository $donRepository, Request $request): Response
     {
         $typeDon = $request->query->get('typeDon');
         $searchTerm = $request->query->get('searchTerm'); // Retrieval of search term
         $offset = $request->query->get('offset', 0); // Default offset is 0
     
         // Define the number of donations to display per page/load
         $limit = 6;
     
         if (!empty($searchTerm)) {
             // If a search term is provided, use a custom method to search for donations
             // Ensure your repository has a method that supports pagination for search results
             $dons = $donRepository->findBySearchTerm($searchTerm);
         } else if (!empty($typeDon)) {
             // If a type is selected but no search term is provided, filter by type with pagination
             $dons = $donRepository->findBy(['Type' => $typeDon], null, $limit, $offset);
         } else {
             // If no specific filter is applied, fetch all donations with pagination
             $dons = $donRepository->findBy([], null, $limit, $offset);
         }
     
         // Fetch unique types of donations for the filter dropdown
         $typesDons = $donRepository->findUniqueDonTypes();
     
         // Determine if there are more donations to load for the "Load More" functionality
         $totalDons = count($dons);
         $hasMore = $totalDons > ($offset + $limit);
     
         return $this->render('don/front_office/index.html.twig', [
             'dons' => $dons,
             'typesDons' => array_column($typesDons, 'Type'),
             'selectedTypeDon' => $typeDon,
             'searchTerm' => $searchTerm,
             'hasMore' => $hasMore, // Pass this flag to the template to conditionally show the "Load More" button
         ]);
     }*/

     #[Route('/', name: 'app_don_index', methods: ['GET'])]
public function index(DonRepository $donRepository, Request $request, Security $security): Response
{
    $typeDon = $request->query->get('typeDon');
    $searchTerm = $request->query->get('searchTerm');
    $offset = $request->query->get('offset', 0);
    $limit = 6;

    // Récupération de l'utilisateur connecté
    $user = $security->getUser();
    // Initialisation de la variable $dons
    $dons = [];

    // Vérifiez si l'utilisateur est connecté et déterminez son rôle
    if ($user) {
        $roles = $user->getRoles();
        if (in_array('ROLE_DIRECTEUR', $roles)) {
            // L'utilisateur est un directeur, affichez tous les dons
            if (!empty($searchTerm)) {
                $dons = $donRepository->findBySearchTerm($searchTerm);
            } else if (!empty($typeDon)) {
                $dons = $donRepository->findBy(['Type' => $typeDon], null, $limit, $offset);
            } else {
                $dons = $donRepository->findBy([], null, $limit, $offset);
            }
        } elseif (in_array('ROLE_DONATEUR', $roles)) {
            // L'utilisateur est un donateur, affichez seulement ses dons
            // Assurez-vous que votre méthode findByUser existe et est correctement implémentée dans votre DonRepository
            $dons = $donRepository->findByUser($user, $typeDon, $searchTerm, $limit, $offset);
        }
    }

    $typesDons = $donRepository->findUniqueDonTypes();
    $totalDons = count($dons);
    $hasMore = $totalDons > ($offset + $limit);

    return $this->render('don/front_office/index.html.twig', [
        'dons' => $dons,
        'typesDons' => array_column($typesDons, 'Type'),
        'selectedTypeDon' => $typeDon,
        'searchTerm' => $searchTerm,
        'hasMore' => $hasMore,
    ]);
}


     
//Affichage back
             //Pagination i added it here in dashboard
             #[Route('/back', name: 'app_don_index_back', methods: ['GET'])]
             public function index_back(Request $request, DonRepository $donRepository, PaginatorInterface $paginator): Response
             {
                 // Création du formulaire de recherche
                 $searchForm = $this->createFormBuilder(null, ['method' => 'GET'])
                 ->add('searchTerm', SearchType::class, [
                     'required' => false,
                     'attr' => [
                         'placeholder' => 'Rechercher...',
                         'class' => 'search-input', // Utilisez cette classe pour styliser dans votre CSS
                         'style' => 'max-width: 200px;', // Ou appliquez le style directement ici
                     ],
                     'label' => false,
                 ])
                 ->getForm();
             
                 $searchForm->handleRequest($request);
             
                 // Initialisation de la variable $donsQuery
                 $donsQuery = $donRepository->findAll(); // Ceci sera ajusté en fonction de la recherche
             
                 if ($searchForm->isSubmitted() && $searchForm->isValid()) {
                     $searchData = $searchForm->getData();
                     // Utilisation de findBySearchTerm pour obtenir les résultats filtrés
                     $donsQuery = $donRepository->findBySearchTerm($searchData['searchTerm']);
                 } else {
                     // Si aucun terme de recherche n'est soumis, récupérer tous les dons
                     $donsQuery = $donRepository->createQueryBuilder('d')->getQuery();
                 }
             
                 // Paginate the results
                 $donsPagination = $paginator->paginate(
                     $donsQuery, // query NOT result, ajusté pour la recherche
                     $request->query->getInt('page', 1), // Define the page parameter, default 1
                     5 // Items per page
                 );
             
                 // Render the view with paginated dons and the search form
                 return $this->render('don/back_office/index_back.html.twig', [
                     'dons' => $donsPagination,
                     'searchForm' => $searchForm->createView(),
                 ]);
             }
    //Add functions back and front

    #[Route('/new/back', name: 'app_don_new_back', methods: ['GET', 'POST'])]
    public function new_index(Request $request, EntityManagerInterface $entityManager,SecurityController $security): Response
    {
        $don = new Don();
        $form = $this->createForm(DonType::class, $don);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $user=$security->getUser();
            $don->setDonateur($user);
            $entityManager->persist($don);
            $entityManager->flush();
            return $this->redirectToRoute('app_don_index_back', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('don/back_office/new_back.html.twig', [
            'don' => $don,
            'form' => $form,
        ]);
    }

    #[Route('/new', name: 'app_don_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, MailjetService $mailjetService, User1Repository $userRepository): Response
    {
        if ($this->isGranted('ROLE_DONATEUR')) {
            $don = new Don();
            $form = $this->createForm(DonType::class, $don);
            $form->handleRequest($request);
    
            if ($form->isSubmitted() && $form->isValid()) {
                $user = $this->getUser(); // Assuming getUser() returns the currently logged-in user
    
                // Ensure that $user is indeed an instance of User1
                if (!$user instanceof User1) {
                    throw new \LogicException('Current user is not a valid User1 instance.');
                }
    
                $don->setDonateur($user);
              
                $entityManager->persist($don);
                $entityManager->flush();
    
                // Sending the email
                $emailContent = "Bienvenu dans HealthSwift. Votre Don a été enregistré avec succès, une date de RDV vous y sera communiquée ultérieurement";
                $mailjetService->sendMail($emailContent, $user->getEmail(), $user->getNom());
    
                return $this->redirectToRoute('app_don_index', [], Response::HTTP_SEE_OTHER);
            }
    
            return $this->renderForm('don/front_office/new.html.twig', [
                'don' => $don,
                'form' => $form,
            ]);
        }
    
        // Optionally, handle the case where the user does not have the ROLE_DONATEUR
        // This could redirect to a different page or display an error message
    }
//show Details
    /*#[Route('/{id}', name: 'app_don_show', methods: ['GET'])]
    public function show(Don $don): Response
    {
        return $this->render('don/front_office/show.html.twig', [
            'don' => $don,
        ]);
    }
*/#[Route('/{id}', name: 'app_don_show', methods: ['GET'])]
public function show(Don $don, DonRepository $donRepository,SecurityController $security): Response
{
    $user =$security->getUser();
    $roles = $user->getRoles();
    
    // Vérifier si l'utilisateur est le directeur ou si le don appartient à l'utilisateur
    if (in_array('ROLE_DIRECTEUR', $roles) || $don->getDonateur() === $user) {
        return $this->render('don/front_office/show.html.twig', [
            'don' => $don,
        ]);
    } else {
        // Optionnel: Rediriger l'utilisateur ou afficher un message d'erreur
        $this->addFlash('error', 'Vous n\'êtes pas autorisé à voir ce don.');
        return $this->redirectToRoute('app_don_index');
    }
}

    #[Route('/back/{id}', name: 'app_don_show_back', methods: ['GET'])]
    public function show_back(Don $don): Response
    {
        return $this->render('don/back_office/show_back.html.twig', [
            'don' => $don,
        ]);
    }

    //Edit
    #[Route('/{id}/edit', name: 'app_don_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Don $don, EntityManagerInterface $entityManager, Security $security): Response
    {
        // Vérifier si l'utilisateur actuel est le donateur qui a créé le don
        if (!$security->isGranted('ROLE_DONATEUR') || $don->getDonateur() !== $security->getUser()) {
            throw new AccessDeniedException('Vous n\'avez pas les droits pour modifier ce don.');
        }

        $form = $this->createForm(DonType::class, $don);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            $this->addFlash('success', 'Le don a été modifié avec succès.');

            return $this->redirectToRoute('app_don_index');
        }

        return $this->renderForm('don/front_office/edit.html.twig', [
            'don' => $don,
            'form' => $form,
        ]);
    }

    #[Route('/edit_back/{id}', name: 'editDon_back', methods: ['GET', 'POST'])]
    public function edit_back(Request $request, Don $don, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(DonType::class, $don);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_don_index_back', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('don/back_office/edit_back.html.twig', [
            'don' => $don,
            'form' => $form,
        ]);
    }
//Delete
#[Route('/{id}', name: 'app_don_delete', methods: ['POST'])]
public function delete(Request $request, Don $don, EntityManagerInterface $entityManager, Security $security): Response
{
    // Vérifier si l'utilisateur actuel est le donateur qui a créé le don
    if (!$security->isGranted('ROLE_DONATEUR') || $don->getDonateur() !== $security->getUser()) {
        throw new AccessDeniedException('Vous n\'avez pas les droits pour supprimer ce don.');
    }

    if ($this->isCsrfTokenValid('delete'.$don->getId(), $request->request->get('_token'))) {
        $entityManager->remove($don);
        $entityManager->flush();

        $this->addFlash('success', 'Le don a été supprimé avec succès.');
    } else {
        $this->addFlash('error', 'Invalid CSRF token.');
    }

    return $this->redirectToRoute('app_don_index');
}

    #[Route('/delete_back/{id}', name: 'delete_don_back', methods: ['POST'])]
    public function delete_back(Request $request, Don $don, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$don->getId(), $request->request->get('_token'))) {
            $entityManager->remove($don);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_don_index_back', [], Response::HTTP_SEE_OTHER);
    }

    

}

