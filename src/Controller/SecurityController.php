<?php

namespace App\Controller;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use App\Entity\User1;
use App\Repository\User1Repository;
use App\Form\ResetPasswordRequestType;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoderInterface;

use Symfony\Component\Security\Core\Encoder\PasswordEncoderInterface; 
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;



class SecurityController extends AbstractController
{
    #[Route(path: '/login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils): Response
    {
        // Check if the user is already authenticated as a donateur
        if ($this->isGranted('ROLE_DONATEUR')  || $this->isGranted('ROLE_DIRECTEUR')) {
            return $this->redirectToRoute('app_home'); // Redirect to donateur dashboard
        }
         // Check if the user is authenticated but doesn't have the expected role
         if ($this->getUser()) {
            throw new AccessDeniedException('You do not have permission to access this page.');
            return $this->render('security/login.html.twig', ['last_username' => $lastUsername, 'error' => $error]);

        }

        // get the login error if there is one
        $error = $authenticationUtils->getLastAuthenticationError();
        // last username entered by the user
        $lastUsername = $authenticationUtils->getLastUsername();

        return $this->render('security/login.html.twig', ['last_username' => $lastUsername, 'error' => $error]);
    }
    #[Route(path: '/mdpoublie', name: 'mdpoublie')]
    public function forgotPassword(Request $request, User1Repository $userRepository, EntityManagerInterface $entityManager, UserPasswordEncoderInterface $passwordEncoder, MailerInterface $mailer)
    {
        $form = $this->createForm(ResetPasswordRequestType::class);
        $form->handleRequest($request);
    
        $emailNotFound = false;
    
        if ($form->isSubmitted() && $form->isValid()) {
            $formData = $form->getData();
            $user = $userRepository->findOneBy(['Email' => $formData['email']]);
    
            if ($user) {
                $newPassword = bin2hex(random_bytes(6)); 
                
                $encodedPassword = $passwordEncoder->encodePassword($user, $newPassword);
                $user->setPassword($encodedPassword);
                $entityManager->persist($user);
                $entityManager->flush();
    
                $email = (new Email())
                    ->from('zighnifiras@gmail.com')
                    ->to($user->getEmail()) 
                    ->subject('Réinitialisation du mot de passe')
                    ->html('Votre nouveau mot de passe est : ' . $newPassword);
                
                $mailer->send($email);
    
                return $this->redirectToRoute('app_login');
            } else {
                $emailNotFound = true;
            }
        }
    
        return $this->render('security/forget_password.html.twig', [
            'form' => $form->createView(),
            'emailNotFound' => $emailNotFound,
        ]);
    }
    
    #[Route(path: '/loginadmin', name: 'admin_login')]
    public function loginadmin(AuthenticationUtils $authenticationUtils): Response
    {
        // Check if the user is already authenticated as an admin
        if ($this->isGranted('ROLE_ADMIN')) {
            return $this->redirectToRoute('statAdmin'); // Redirect to admin dashboard
        }
         // Check if the user is authenticated but doesn't have the expected role
         if ($this->getUser()) {
            throw new AccessDeniedException('You do not have permission to access this page.');
            return $this->render('security/admin_login.html.twig', ['last_username' => $lastUsername, 'error' => $error]);

        }

    

        // get the login error if there is one
        $error = $authenticationUtils->getLastAuthenticationError();
        // last username entered by the user
        $lastUsername = $authenticationUtils->getLastUsername();

        return $this->render('security/admin_login.html.twig', ['last_username' => $lastUsername, 'error' => $error]);
    }


    #[Route(path: '/logout', name: 'app_logout')]
    public function logout(): void
    {
        throw new \LogicException('This method can be blank - it will be intercepted by the logout key on your firewall.');

    }


    
    
  
}

