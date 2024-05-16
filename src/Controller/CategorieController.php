<?php

namespace App\Controller;

use App\Entity\Categorie;
use App\Form\CategorieType;
use App\Repository\CategorieRepository;
use Doctrine\ORM\EntityManager;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CategorieController extends AbstractController
{
    #[Route('/categorie', name: 'app_categorie')]
    public function index(): Response
    {
        return $this->render('categorie/index.html.twig', [
            'controller_name' => 'CategorieController',
        ]);
    }

    #[Route('/showCategorie', name: 'showCategorie')]
    public function showCategorie(CategorieRepository $repo): Response
    {
        $categorie = $repo->findAll();
        return $this->render('categorie/showCategorie.html.twig', [
            'categorie' => $categorie
        ]);
    }

    #[Route('/addCategorie', name: 'addCategorie')]
public function addCategorie(EntityManagerInterface $em, Request $request, CategorieRepository $repo)
{
    $categorie = new Categorie();
    $form = $this->createForm(CategorieType::class, $categorie);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $libelleCategorie = $categorie->getLibelleCategorie();
        $existingCategorie = $repo->findOneBy(['LibelleCategorie' => $libelleCategorie]);
        
        if ($existingCategorie) {
            $this->addFlash(
                'error',
                'Le libellé de catégorie existe déjà.'
            );
            return $this->redirectToRoute('addCategorie');
        } else {
            $em->persist($categorie);
            $em->flush();
            $this->addFlash(
                'success',
                'Category added.'
            );
            return $this->redirectToRoute('showCategorie');
        }
    }
    return $this->render('categorie/addCategorie.html.twig', [
        'f' => $form->createView()
    ]);
}

   

    #[Route('/editCategorie/{id}', name: 'editCategorie')]
    public function editCategorie($id, CategorieRepository $repo, EntityManagerInterface $em, Request $request): Response
    {
        $categorie = $repo->find($id);
        $form = $this->createForm(CategorieType::class, $categorie);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $em->persist($categorie);
            $em->flush();
            $this->addFlash(
                'success',
                'Category edited.'
            );
            return $this->redirectToRoute('showCategorie');
        }

        return $this->render('categorie/addCategorie.html.twig', [
            'f' => $form->createView()
        ]);
    }

    #[Route('/deleteCategorie/{id}', name: 'deleteCategorie')]
    public function deleteCategorie($id, CategorieRepository $repo, EntityManagerInterface $em): Response
    {
        $categorie = $repo->find($id);
        $em->remove($categorie);
        $em->flush();
        $this->addFlash(
            'success',
            'Category deleted.'
        );
        return $this->redirectToRoute('showCategorie');
    }
}
