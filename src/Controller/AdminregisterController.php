<?php

namespace App\Controller;
use App\Service\UserService;

use App\Entity\User1;
use App\Form\RegistrationFormType;
use App\Form\UpdateType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Form\Extension\Core\Type\SearchType;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoderInterface;
use Symfony\Component\Security\Core\Security;
use App\Repository\User1Repository;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Mailer\Exception\TransportExceptionInterface;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\Mailer\Bridge\Google\Transport\GmailSmtpTransport;
use Symfony\Component\Mailer\Mailer;
use Symfony\Component\Mime\Email;
use Symfony\Component\Form\FormError; // Add this line
class AdminregisterController extends AbstractController
{
    #[Route('/list', name: 'list_user')]
public function list(User1Repository $userRepository, Request $request): Response
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

    // Récupérer les paramètres de tri de la requête
    $orderBy = $request->query->get('orderBy', 'id'); // Par défaut, trier par ID si aucun tri spécifié
    $orderDirection = 'ASC'; // Par défaut, tri croissant
    if ($orderBy === 'desc') {
        $orderDirection = 'DESC';
    }

    // Utiliser la méthode findBy avec l'ordre spécifié et la recherche si un terme de recherche est soumis
    if ($searchForm->isSubmitted() && $searchForm->isValid()) {
        $searchData = $searchForm->getData();
        // Utiliser findBySearchTerm pour filtrer les utilisateurs avec recherche et tri
        $users = $userRepository->findBySearchTerm($searchData['searchTerm'], ['id' => $orderDirection]);
    } else {
        // Sinon, utiliser findAll mais avec une Query pour la pagination
        $users = $userRepository->findBy([], ['id' => $orderDirection]);
    }

    // Sinon, afficher la page complète avec le formulaire de recherche et la liste des utilisateurs
    return $this->render('user/list.html.twig', [
        'users' => $users,
        'searchForm' => $searchForm->createView()
    ]);
}


#[Route('/detail/{id}', name: 'detail_user')]
    public function detail(int $id, User1Repository $userRepository): Response
    { 
        $user = $userRepository->find($id);

        if (!$user) {
            throw $this->createNotFoundException('User not found');
        }

        return $this->render('user/detail.html.twig', [
            'user' => $user
        ]);
    }
#[Route('/user/delete/{id}', name: 'deleteuser')]
    public function deleteUser(int $id, EntityManagerInterface $entityManager, User1Repository $userRepository): Response
    {
        $user = $userRepository->find($id);

        if (!$user) {
            throw $this->createNotFoundException('User not found');
        }

        $entityManager->remove($user);
        $entityManager->flush();

        return $this->redirectToRoute('list_user');

    }
    #[Route('/admin/edit/{id}', name: 'updateadmin', methods: ['GET', 'POST'])]
public function updateadmin(Request $request, EntityManagerInterface $entityManager,int $id, User1Repository $userRepository, UserPasswordEncoderInterface $passwordEncoder): Response
{
    $user1 = $userRepository->find($id);

    
    $form = $this->createForm(UpdateType::class, $user1);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        if ($form->get('password')->getData()) {
            $user1->setPassword(
                $passwordEncoder->encodePassword($user1, $form->get('password')->getData())
            );
        }

        // Persist and flush the changes
        $entityManager->persist($user1);
        $entityManager->flush();

        // Redirect or do any other actions upon successful update
        return $this->redirectToRoute('updateadmin', ['id' => $id]);
    }
    

    // If form is submitted but not valid, render template with form and errors
    if ($form->isSubmitted()) {
        return $this->render('adminregister/profileadmin.html.twig', [
            'user' => $user1,
            'form' => $form->createView(),
            'formSubmitted' => true,
        ]);
    }

    return $this->render('adminregister/profileadmin.html.twig', [
        'user' => $user1,
        'form' => $form->createView(),
        'formSubmitted' => false,
    ]);
}  

 
#[Route('/statAdmin', name: 'statAdmin')]
public function statAdmin(User1Repository $userRepository): Response
    {
        $stats = $userRepository->getUsersCountByRole();
        foreach ($stats as $key => $stat) {
            if (in_array('ROLE_ADMIN', $stat['roles'])) {
                unset($stats[$key]);
            }
        }

        return $this->render('user/dashboard.html.twig', [
            'stats' => $stats,
        ]);
    }


   
    
}