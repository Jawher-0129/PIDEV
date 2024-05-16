<?php

namespace App\Controller;

use App\Entity\Materiel;
use App\Form\MaterielType;
use App\Repository\CategorieRepository;
use App\Repository\MaterielRepository;
use Doctrine\ORM\EntityManager;
use Doctrine\ORM\EntityManagerInterface;
use Dompdf\Dompdf;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Endroid\QrCode\ErrorCorrectionLevel\ErrorCorrectionLevelLow;
use Endroid\QrCode\ErrorCorrectionLevel\ErrorCorrectionLevelHigh;
use Endroid\QrCode\ErrorCorrectionLevel;
use Endroid\QrCode\Writer\PngWriter;
use Endroid\QrCode\Label\Alignment\LabelAlignmentCenter;
use Endroid\QrCode\Label\Font\NotoSans;
use Endroid\QrCode\Label\Label;
use Endroid\QrCode\Label\Margin\Margin;
use Endroid\QrCode\Logo\Logo;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Color\Color;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel as QrCodeErrorCorrectionLevel;
use Symfony\Component\Security\Core\Security;

class MaterielController extends AbstractController
{
    #[Route('/materiel', name: 'app_materiel')]
    public function index(): Response
    {
        return $this->render('materiel/index.html.twig', [
            'controller_name' => 'MaterielController',
        ]);
    }

    #[Route('/showMaterielAdmin', name: 'showMaterielAdmin')]
    public function showMaterielAdmin(MaterielRepository $repo): Response
    {
        $materiel=$repo->findAll();
        foreach ($materiel as $m) {
            if($m->getImage()[0]=='C')
            {
            $pathInfo = pathinfo($m->getImage());
            $m->setImage($pathInfo['basename']);
            }
        }

        return $this->render('materiel/showMaterielAdmin.html.twig',[
            'materiel'=>$materiel
        ]); 
    }


    #[Route('/addMateriel', name: 'addMateriel')]
    public function addMateriel(MaterielRepository $repo, EntityManagerInterface $em,Request $request,Security $security)
    {
        $materiel = new Materiel();
        $form = $this->createForm(MaterielType::class, $materiel);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $user = $security->getUser();
            $materiel->setIdUser($user);
            $fileUpload = $form->get('Image')->getData();
            $fileName = md5(uniqid()) . '.' . $fileUpload->guessExtension();
            $fileUpload->move($this->getParameter('kernel.project_dir') . '/public/uploads', $fileName);// Creation dossier uploads
            $materiel->setImage($fileName);

            //GENEREATE QR CODE

           $url = 'https://www.google.com/search?q=';

            $objDateTime = new \DateTime('NOW');
            $dateString = $objDateTime->format('d-m-Y H:i:s');

            $path = dirname(__DIR__, 2) . '/public/';


            // set qrcode
            $result = Builder::create()
                ->writer(new PngWriter())
                ->writerOptions([])
                ->data('Material Name: ' . $materiel->getLibelle() . "\n"
                    . 'Material Price: ' . $materiel->getPrix() . "\n"
                    . 'Material Description: ' . $materiel->getDescription() . "\n"
                    . 'Material Disponibility: ' . $materiel->getDisponibilite() . "\n"
                    //. 'Material Categorie: ' . $materiel->getCategorieLibelle() . "\n"
                )
                ->encoding(new Encoding('UTF-8'))
                ->errorCorrectionLevel(ErrorCorrectionLevel::High)
                ->size(400)
                ->margin(10)
                ->labelText($dateString)
                //->labelAlignment(new LabelAlignement())
                ->labelMargin(new Margin(15, 5, 5, 5))
                ->logoPath($path . 'uploads/' . $fileName)
                ->logoResizeToWidth('100')
                ->logoResizeToHeight('100')
                ->backgroundColor(new Color(255, 255, 255))
                ->build();

            //generate name
           // $namePng = uniqid('', '') . '.png';
          
            $em->persist($materiel);
            $em->flush();
            $qrCodeFileName = $materiel->getidMateriel() . '.png';

            //Save img png
            $result->saveToFile($path . 'uploads/' . $qrCodeFileName);
            $result->getDataUri();
            $this->addFlash(
                'success',
                'Material added.'
            );
            return $this->redirectToRoute('showMaterielAdmin');
        }

        return $this->render('materiel/addMateriel.html.twig', [
            'f' => $form->createView()
        ]);
    }

    #[Route('/deleteMateriel/{id}', name: 'deleteMateriel')]
    public function deleteMateriel($id,MaterielRepository $repo,EntityManagerInterface $em): Response
    {
        $materiel=$repo->find($id);
        $em->remove($materiel);
        
        $em->flush();

        $this->addFlash(
            'success', // Utilisez 'success' ou tout autre nom que vous préférez
            'Material deleted.'
        );
    
        return $this->redirectToRoute('showMaterielAdmin');
    }


    #[Route('/editMateriel/{id}/{imageUrl}', name: 'editMateriel')]
    public function editMateriel($id,$imageUrl,MaterielRepository $repo, EntityManagerInterface $em,Request $request)
    {
        $materiel =$repo->find($id); 
        $form = $this->createForm(MaterielType::class, $materiel);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $fileUpload = $form->get('Image')->getData();
            if ($fileUpload) {
                $fileName = md5(uniqid()) . '.' . $fileUpload->guessExtension();
                $fileUpload->move($this->getParameter('kernel.project_dir') . '/public/uploads', $fileName);
                $materiel->setImage($fileName);
            }
            else
            {
                $materiel->setImage($imageUrl);
            }

            $em->persist($materiel);
            $em->flush();
            $this->addFlash(
                'success',
                'Material edited.'
            );
            return $this->redirectToRoute('showMaterielAdmin');
        }
        return $this->render('materiel/addMateriel.html.twig', [
            'f' => $form->createView()
        ]);
    }

    #[Route('/showCategorieFront', name: 'showCategorieFront')]
    public function showCategorieFront(MaterielRepository $repomat,CategorieRepository $repocat): Response
    {
        $Categorie=$repocat->findAll();
        return $this->render('materiel/showCategorieFront.html.twig',[
            'categories'=>$Categorie
        ]); 
    }

    #[Route('/materiels_par_categorie/{id}', name: 'materiels_par_categorie')]
    public function materielsParCategorie($id, CategorieRepository $categorieRepository,MaterielRepository $materielRepository): Response
    {
    $categorie = $categorieRepository->find($id);
    //$materiels = $materielRepository->findByCategorie($categorie);
    return $this->render('materiel/materiels_par_categorie.html.twig', [
        'categorie' => $categorie,
       // 'materiels' => $materiels

    ]);
    }

    #[Route('/exportpdf', name: 'exportpdf')]
    public function exportToPdf(MaterielRepository $repository): Response
{
    // Récupérer les données de matériel depuis votre base de données
    $materiels = $repository->findAll();

    // Créer le tableau de données pour le PDF
    $tableData = [];
    foreach ($materiels as $materiels) {
        $tableData[] = [
            'idMateriel' => $materiels->getidMateriel(),
            'Libelle' => $materiels->getLibelle(),
            'Description' => $materiels->getDescription(),
            'Disponibilite' => $materiels->getDisponibilite(),
            'Prix' => $materiels->getPrix(),
            'Libelle' => $materiels->getCategorieLibelle(),
        ];
    }

    // Créer le PDF avec Dompdf
    $dompdf=new Dompdf();
    $html = $this->renderView('materiel/export-pdf.html.twig', [
        'tableData' => $tableData,
    ]);
    $dompdf->loadHtml($html);
    $dompdf->setPaper('A4', 'landscape');
    $dompdf->render();

    // Envoyer le PDF au navigateur
    $response = new Response($dompdf->output(), 200, [
        'Content-Type' => 'application/pdf',
        'Content-Disposition' => 'attachment; filename="materiels.pdf"',
    ]);
    return $response;
}

    #[Route('/TriCroissant', name: 'TriCroissant')]
    public function TriCroissant(MaterielRepository $repo): Response
    {
    $materiel = $repo->TriCroissant();
    return $this->render('materiel/index.html.twig', [
        'materiel' => $materiel
    ]);
    }

    #[Route('/TriDecroissant', name: 'TriDecroissant')]
    public function TriDecroissant(MaterielRepository $repo): Response
    {
    $materiel = $repo->TriDecroissant();
    return $this->render('materiel/index.html.twig', [
        'materiel' => $materiel
    ]);
    }

    #[Route('/searchMaterielFront', name: 'searchMaterielFront')]
    public function searchMaterielFront(Request $request, MaterielRepository $repo): Response
    {
     $query = $request->query->get('query');
     $materiels = [];

        if ($query) {
         $materiels = $repo->findByLibelle($query);
     } else {
         $materiels = $repo->findAll();
     }

     return $this->render('materiel/indexFront.html.twig', [
         'materiels' => $materiels,
         'query' => $query,
     ]);
    }


    #[Route('/searchMateriel', name: 'searchMateriel')]
    public function searchMateriel(Request $request, MaterielRepository $repo): Response
    {
     $query = $request->query->get('query');
     $materiels = [];

        if ($query) {
         $materiels = $repo->findByLibelle($query);
     } else {
         $materiels = $repo->findAll();
     }

     return $this->render('materiel/index.html.twig', [
         'materiel' => $materiels,
         'query' => $query,
     ]);
    }

    #[Route('/statMateriel', name: 'stat_materiel')]
    public function VoitureStatistics( MaterielRepository $repo): Response
    {
        $total = $repo->countByDisponibilite('0') + $repo->countByDisponibilite('1');
        $oui = $repo->countByDisponibilite('1');
        $non = $repo->countByDisponibilite('0');


        $ouiPercentage = round(($oui / $total) * 100);
        $nonPercentage = round(($non / $total) * 100);

        return $this->render('materiel/stat.html.twig', [
            'Disponibles' => $ouiPercentage,
            'nondisponibles' => $nonPercentage,
        ]);
    }
    


}
