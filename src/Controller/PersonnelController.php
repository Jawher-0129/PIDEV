<?php

namespace App\Controller;

use App\Entity\Personnel;
use App\Form\PersonnelType;
use App\Repository\PersonnelRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\String\Slugger\SluggerInterface;

#[Route('/personnel')]
class PersonnelController extends AbstractController
{
    #[Route('/', name: 'app_personnel_index', methods: ['GET'])]
    public function index(Request $request, PersonnelRepository $personnelRepository): Response
    {
        $limit = 5; 
        $page = max($request->query->getInt('page', 1), 1);
        $totalPersonnels = $personnelRepository->count([]);
        $totalPages = ceil($totalPersonnels / $limit);
        $offset = ($page - 1) * $limit;

        $personnels = $personnelRepository->findBy([], ['id_personnel' => 'ASC'], $limit, $offset);

        return $this->render('personnel/index.html.twig', [
            'personnels' => $personnels,
            'totalPages' => $totalPages,
            'currentPage' => $page,
        ]);
    }

    #[Route('/frontPersonnel', name: 'app_personnel_front', methods: ['GET'])]
    public function front(PersonnelRepository $personnelRepository): Response
    {
        return $this->render('personnel/front.html.twig', [
            'personnels' => $personnelRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_personnel_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $personnel = new Personnel();
        $form = $this->createForm(PersonnelType::class, $personnel);
    
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            // Handle potential image upload
            $imageFile = $form->get('Image')->getData(); 
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();
    
                try {
                    $imageFile->move(
                        $this->getParameter('images_directory'),
                        $newFilename
                    );
                } catch (FileException $e) {
    
                }
    
                $personnel->setImage($newFilename);
            }

            // Handle rating submission
            $rating = $form->get('rating')->getData();
            $personnel->setRating($rating);
    
            $entityManager->persist($personnel);
            $entityManager->flush();
    
            return $this->redirectToRoute('app_personnel_index', [], Response::HTTP_SEE_OTHER);
        }
    
        return $this->renderForm('personnel/new.html.twig', [
            'personnel' => $personnel,
            'form' => $form,
        ]);
    }

    #[Route('/{id_personnel}', name: 'app_personnel_show', methods: ['GET'])]
    public function show(Personnel $personnel): Response
{
    if (!$personnel) {
        throw new NotFoundHttpException('Personnel not found.');
    }

    return $this->render('personnel/show.html.twig', [
        'personnel' => $personnel,
    ]);
}

    #[Route('/{id_personnel}/edit', name: 'app_personnel_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Personnel $personnel, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(PersonnelType::class, $personnel);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_personnel_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('personnel/edit.html.twig', [
            'personnel' => $personnel,
            'form' => $form,
        ]);
    }

    #[Route('/{id_personnel}', name: 'app_personnel_delete', methods: ['POST'])]
    public function delete(Request $request, Personnel $personnel, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$personnel->getIdPersonnel(), $request->request->get('_token'))) {
            $entityManager->remove($personnel);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_personnel_index', [], Response::HTTP_SEE_OTHER);
    }
    #[Route('/search', name: 'app_personnel_show')]
    public function search(Request $request, PersonnelRepository $personnelRepository): Response
    {
        $nom = $request->query->get('nom');
        $role = $request->query->get('role');

        $personnels = $personnelRepository->findByNomAndRole($nom, $role);

        return $this->render('personnel/search.html.twig', [
            'personnels' => $personnels,
        ]);
    }
}