<?php

namespace App\Controller;

use App\Entity\User1;
use App\Form\UserType;
use App\Repository\User1Repository;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Mailer\Exception\TransportExceptionInterface;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoderInterface;
use Symfony\Component\Security\Core\Security;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Mailer\Bridge\Google\Transport\GmailSmtpTransport;
use Symfony\Component\Mailer\Mailer;
use Symfony\Component\Mime\Email;
use Symfony\Component\Form\FormError; // Add this line
use App\Form\UpdateType;

class UserController extends AbstractController
{
    #[Route('/register', name: 'user_register')]
    public function register(Request $request, UserPasswordEncoderInterface $passwordEncoder, User1Repository $userRepository): Response
    {
        $user = new User1();
        $form = $this->createForm(UserType::class, $user);
        $form->handleRequest($request);

        $email = $form->get('email')->getData();

        // Check if the email already exists
        $existingUser = $userRepository->findOneBy(['Email' => $email]);

        if ($existingUser) {
            // Add a form error for the email field
            $form->get('email')->addError(new FormError('Email already exists.'));

            // Render the registration form with error message
            return $this->render('user/register.html.twig', [
                'form' => $form->createView(),
            ]);
        }

        if ($form->isSubmitted() && $form->isValid()) {
            // Encode the plain password
            $user->setPassword(
                $passwordEncoder->encodePassword($user, $form->get('password')->getData())
            );

            // Save the user to the database
            $entityManager = $this->getDoctrine()->getManager();
            $entityManager->persist($user);
            $entityManager->flush();

            return $this->redirectToRoute('app_login');
        }

        return $this->render('user/register.html.twig', [
            'form' => $form->createView(),
            'formErrors' => $form->getErrors(true, false), // Pass form errors explicitly

        ]);
    }



    #[Route('/user/update', name: 'updateuser', methods: ['GET', 'POST'])]
    public function updateUser(Request $request, EntityManagerInterface $entityManager, Security $security, UserPasswordEncoderInterface $passwordEncoder): Response
    {
        $user = $security->getUser();
    
        // Ensure the user is authenticated
        if (!$user instanceof User1) {
            throw $this->createAccessDeniedException('User is not authenticated.');
        }
    
        $form = $this->createForm(UpdateType::class, $user);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            if ($form->get('password')->getData()) {
                $user->setPassword(
                    $passwordEncoder->encodePassword($user, $form->get('password')->getData())
                );
            }
    
            // Persist and flush the changes
            $entityManager->persist($user);
            $entityManager->flush();
    
            // Redirect or do any other actions upon successful update
            return $this->redirectToRoute('updateuser');
        }
        
        // If form is submitted but not valid, render template with form and errors
        if ($form->isSubmitted()) {
            return $this->render('user/profile.html.twig', [
                'user' => $user,
                'form' => $form->createView(),
                'formSubmitted' => true,
            ]);
        }

        return $this->render('user/profile.html.twig', [
            'user' => $user,
            'form' => $form->createView(),
            'formSubmitted' => false,
        ]);
    }
   
   

}
    