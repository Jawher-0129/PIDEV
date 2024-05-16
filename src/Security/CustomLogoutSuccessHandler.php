<?php
namespace App\Security;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Security\Http\Logout\LogoutSuccessHandlerInterface;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\Security\Core\Authorization\AuthorizationCheckerInterface;

class CustomLogoutSuccessHandler implements LogoutSuccessHandlerInterface
{
    private $tokenStorage;
    private $authorizationChecker;

    public function __construct(TokenStorageInterface $tokenStorage, AuthorizationCheckerInterface $authorizationChecker)
    {
        $this->tokenStorage = $tokenStorage;
        $this->authorizationChecker = $authorizationChecker;
    }

    public function onLogoutSuccess(Request $request): Response
    {
        if ($this->authorizationChecker->isGranted('ROLE_ADMIN')) {
            // If the user is an admin, redirect to admin login page
            return new RedirectResponse('/loginadmin');
        } else {
            // Redirect to user login page by default
            return new RedirectResponse('/login');
        }
    }
}
