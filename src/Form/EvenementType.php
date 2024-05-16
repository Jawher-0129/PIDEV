<?php

namespace App\Form;

use App\Entity\Evenement;
use App\Entity\Actualite;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\File;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Positive;
use Symfony\Component\Validator\Constraints\GreaterThan;

class EvenementType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('Titre', null, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'Please enter a title.'
                    ]),
                ],
            ])
            ->add('Date', null, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'La date ne peut pas être vide.'
                    ]),
                    new GreaterThan([
                        'value' => new \DateTime(),
                        'message' => 'La date doit être ultérieure à aujourd\'hui.'
                    ]),
                ],
            ])
            ->add('Duree', null, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'La durée ne peut pas être vide.'
                    ]),
                    new Positive([
                        'message' => 'La durée doit être un nombre positif.'
                    ]),
                ],
            ])
            ->add('Lieu', null, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'Please enter a location.'
                    ]),
                ],
            ])
            ->add('Objectif', null, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'Please enter an objective.'
                    ]),
                ],
            ])
            ->add('Image', FileType::class, [
                'data_class' => null,
                'constraints' => [
                    new NotBlank([
                        'message' => 'Please upload an image.'
                    ]),
                    new File([
                        'maxSize' => '1024k', 
                        'mimeTypes' => [
                            'image/gif',
                            'image/jpeg',
                            'image/png',
                            'image/jpg', 
                        ],
                        'mimeTypesMessage' => 'Please upload a valid image',
                    ])
                ]
            ])
            ->add('actualite', EntityType::class, [ 
                'class' => Actualite::class,
                'choice_label' => 'Titre',
                'placeholder' => 'Choose an actualite',
                'constraints' => [
                    new NotBlank([
                        'message' => 'Please choose an actualite.'
                    ]),
                ],
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Evenement::class,
        ]);
    }
}