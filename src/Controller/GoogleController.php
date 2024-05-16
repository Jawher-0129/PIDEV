<?php

namespace App\Controller;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class GoogleController extends AbstractController
{
   /**
     * Link to this controller to start the "connect" process
     *
     * @Route("/connect/google", name="connect_google")
     * @param ClientRegistry $clientRegistry
     * @return RedirectResponse
     */
    public function connectAction(ClientRegistry $clientRegistry)
    {
        $scopes = [];
        $options = [];
    // Redirect the user to Google for authentication
    return $clientRegistry
        ->getClient('google') // key used in config/packages/knpu_oauth2_client.yaml
        // Les scopes que vous souhaitez (laissez vide pour utiliser les valeurs par défaut)
 // Les options supplémentaires à passer à la méthode getAuthorizationUrl() du fournisseur
        ->redirect($scopes, $options);
    }

    /**
     * After going to Facebook, you're redirected back here
     * because this is the "redirect_route" you configured
     * in config/packages/knpu_oauth2_client.yaml
     *
     * @Route("/connect/google/check", name="connect_google_check")
     * @param Request $request
     * @return JsonResponse|RedirectResponse
     */
    public function connectCheckAction(Request $request)
    {
       if(!$this->getUser()){
           return new JsonResponse(array('status'=>false,'message'=>"user not found!"));

       }else{
           return $this->redirectToRoute('app_home');
       }
    }
}