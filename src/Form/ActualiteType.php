<?php

namespace App\Form;

use App\Entity\Actualite;
use App\Entity\Evenement;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Choice;


class ActualiteType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('Titre', null, [
            'constraints' => [
                new NotBlank([
                    'message' => 'Veuillez saisir un titre.'
                ]),
            ],
        ])
        ->add('Description', null, [
            'constraints' => [
                new NotBlank([
                    'message' => 'Veuillez saisir une description.'
                ]),
            ],
        ])
        ->add('Type_pub_cible', ChoiceType::class, [
            'choices' => [
                'Enfant' => 'enfant',
                'Femme' => 'femme',
                'Adulte' => 'adulte',
                'Adolescent' => 'adolescent',
            ],
            'constraints' => [
                new NotBlank([
                    'message' => 'Veuillez choisir un type de public cible.'
                ]),
                new Choice([
                    'choices' => ['enfant', 'femme', 'adulte', 'adolescent'],
                    'message' => 'Veuillez choisir une option valide.',
                ]),
            ],
            'placeholder' => 'Choisir un type de public cible', 
        ])
        ->add('Theme', ChoiceType::class, [
            'choices' => [
                'Cancer' => 'Cancer',
                'Nutrition' => 'Nutrition',
                'Maternal and Child Health' => 'Maternal and Child Health',
                'Diabète' => 'Diabète',
                'Santé mentale' => 'Santé mentale',
                'Maladies infectieuses' => 'Maladies infectieuses',
            ],
            'constraints' => [
                new NotBlank([
                    'message' => 'Veuillez choisir un thème.'
                ]),
                new Choice([
                    'choices' => ['Cancer', 'Nutrition', 'Maternal and Child Health', 'Diabète', 'Santé mentale', 'Maladies infectieuses'],
                    'message' => 'Veuillez choisir une option valide.',
                ]),
            ],
            'placeholder' => 'Choisir un thème',
        ])
        ->add('Evenement', EntityType::class, [
            'class' => Evenement::class,
            'choice_label' => function (Evenement $evenement) {
                return $evenement->getTitre(); 
            },
            'required' => false, // Make the field optional
        ])        
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Actualite::class,
        ]);
    }
}