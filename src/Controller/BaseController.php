<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class BaseController extends AbstractController
{
  

    #[Route('/home', name: 'app_home')]
    public function index1(): Response
    {// Assuming you need to pass the formSubmitted variable
    $formValid = true; // Adjust this value according to your logic

    return $this->render('base.html.twig', [
        'formValid' => $formValid,

    ]);
    }

    


}
