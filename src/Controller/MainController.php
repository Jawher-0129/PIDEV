<?php

namespace App\Controller;

use App\Entity\RendezVous;
use App\Repository\RendezVousRepository;
use DateTime;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class MainController extends AbstractController
{
    #[Route('/calendrier', name: 'app_calendrier')]
    public function index(RendezVousRepository $calendar): Response
    {
        $events = $calendar->findAll();

        $rdvs = [];

        foreach($events as $event){
            $rdvs[] = [
                'id' => $event->getId_rendezvous(),
                'start' => $event->getdate()->format('Y-m-d H:i:s'),
               /*  'end' => $event->getEnd()->format('Y-m-d H:i:s'), */
                'title' => $event->getLieu(),
               /*  'description' => $event->getDescription(), */
                /* 'backgroundColor' => $event->getBackgroundColor(),
                'borderColor' => $event->getBorderColor(),
                'textColor' => $event->getTextColor(),
                'allDay' => $event->getAllDay(),  */
            ];
        }

        $data = json_encode($rdvs);

        return $this->render('main/index.html.twig', compact('data'));
    }
    #[Route('/api/{id}/edit', name: 'app_api',methods: ['PUT'])]
    public function majEvent(?RendezVous $calendar, Request $request,EntityManagerInterface $entityManager): Response
    {// On récupère les données
    $donnees = json_decode($request->getContent());

    if(
        isset($donnees->title) && !empty($donnees->title) &&
        isset($donnees->start) && !empty($donnees->start) 
       /*  isset($donnees->description) && !empty($donnees->description) && */
       /*  isset($donnees->backgroundColor) && !empty($donnees->backgroundColor) &&
        isset($donnees->borderColor) && !empty($donnees->borderColor) &&
        isset($donnees->textColor) && !empty($donnees->textColor) */
    ){
        // Les données sont complètes
        // On initialise un code
        $code = 200;

        // On vérifie si l'id existe
        if(!$calendar){
            // On instancie un rendez-vous
            $calendar = new RendezVous;

            // On change le code
            $code = 201;
        }

        // On hydrate l'objet avec les données
        $calendar->setLieu($donnees->title);
       /*  $calendar->setDescription($donnees->description); */
        $calendar->setDate(new DateTime($donnees->start));
       /*  if($donnees->allDay){
            $calendar->setEnd(new DateTime($donnees->start));
        }else{
            $calendar->setEnd(new DateTime($donnees->end));
        }
        $calendar->setAllDay($donnees->allDay);
        $calendar->setBackgroundColor($donnees->backgroundColor);
        $calendar->setBorderColor($donnees->borderColor);
        $calendar->setTextColor($donnees->textColor); */
        $entityManager->persist($calendar);
        $entityManager->flush();

        // On retourne le code
        return new Response('Ok', $code);
    }else{
        // Les données sont incomplètes
        return new Response('Données incomplètes', 404);
    }


    return $this->render('api/index.html.twig', [
        'controller_name' => 'ApiController',
    ]);
}
}
