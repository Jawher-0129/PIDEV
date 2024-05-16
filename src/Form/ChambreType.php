<?php

namespace App\Form;

use App\Entity\Chambre;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Positive;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;

class ChambreType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('Numero', NumberType::class, [
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

            ->add('Disponibilite', ChoiceType::class, [
                'choices' => [
                    'OUI' => 'oui', 
                    'NON' => 'non',
                ],
                'expanded' => true, 
                'multiple' => false, 
                'label' => 'Disponibilité', 
            ])
            ->add('Nombre_litsTotal', NumberType::class, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'Ce champ ne peut pas être vide.',
                    ]),
                    new Positive([
                        'message' => ' Le nombre de lits total doit être un nombre positif supérieur à zéro.',
                    ]),
                ],
                'attr' => [
                    'min' => 1,
                    'step' => 1, 
                ],
                'required' => true,
            ])

            ->add('Nmbr_litsDisponible', NumberType::class, [
                'constraints' => [
                    new NotBlank([
                        'message' => 'Ce champ ne peut pas être vide.',
                    ]),
                    new Positive([
                        'message' => ' Le nombre de lits disponible doit être un nombre positif supérieur où égale à zéro.',
                    ]),
                ],
                'attr' => [
                    'min' => 0,
                    'step' => 1, 
                ],
                'required' => true,
            ])
            ->add('personnel')
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Chambre::class,
        ]);
    }
}