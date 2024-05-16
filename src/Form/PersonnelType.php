<?php

namespace App\Form;

use App\Entity\Personnel;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\File;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Validator\Constraints\Positive;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;

class PersonnelType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('Nom')
            ->add('Prenom_personnel')
            ->add('Disponibilite', ChoiceType::class, [
                'choices' => [
                    'OUI' => 'oui', 
                    'NON' => 'non',
                ],
                'expanded' => true, 
                'multiple' => false, 
                'label' => 'Disponibilité', 
            ])
            ->add('Role', ChoiceType::class, [
                'choices' => [
                    'Cardiologie' => 'Cardiologie',
                    'Chirurgie' => 'Chirurgie',
                    'Radiologie' => 'Radiologie',
                    'Neurologie' => 'Neurologie',
                    'Anesthésiologie' => 'Anesthesiologie',
                    'Infirmier' => 'Infirmier',
                    'Médecine Générale' => 'MedecineGenerale',
                ],
                'placeholder' => 'Choisis un role', 
                'attr' => ['class' => 'form-control'],
            ])
            ->add('Experience', NumberType::class, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'Ce champ ne peut pas être vide.',
                    ]),
                    new Positive([
                        'message' => 'L\'expérience doit être un nombre positif supérieur à zéro.',
                    ]),
                ],
                'attr' => [
                    'min' => 1,
                    'step' => 1, 
                ],
                'required' => true,
            ])
            ->add('rating', NumberType::class, [
                'label' => 'Rating',
                'constraints' => [
                    new Positive([
                        'message' => 'Le rating doit être un nombre positif supérieur à zéro.',
                    ]),
                ],
                'attr' => [
                    'min' => 1,
                    'max' => 5, 
                    'step' => 1,
                ],
                'required' => true,
            ])
            ->add('Image', FileType::class, [
                'data_class' => null,
                'attr' => [
                    'class' => 'custom-file-input', 
                ],
                'constraints' => [
                    new File([
                        'maxSize' => '1024k', 
                        'mimeTypes' => [
                            'image/gif',
                            'image/jpeg',
                            'image/png',
                        ],
                        'mimeTypesMessage' => 'Please upload a valid image',
                    ]),
                ],
            ])
            ->add('submit', SubmitType::class, [
                'label' => 'Enregistrer',
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Personnel::class,
        ]);
    }
}