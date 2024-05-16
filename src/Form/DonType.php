<?php

namespace App\Form;

use App\Entity\Don;

use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\GreaterThan;
use Symfony\Component\Validator\Constraints\GreaterThanOrEqual;
use Symfony\Component\Validator\Constraints\NotNull;

class DonType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('Type', ChoiceType::class, [
                'label' => 'Type de don',
                'choices' => [
                    'Don financier' => 'don_financier',
                    'Don de matériel' => 'don_de_materiel',
                    'Don de compétences' => 'don_de_competences',
                    'Don de nourriture' => 'don_de_nourriture',
                    'Don de vêtements' => 'don_de_vetements',
                    'Don de sang' => 'don_de_sang',
                    'Don de Medicaments' => 'don_de_Medicaments',
                ],
            ])
            ->add('Date_remise', DateType::class, [
                'required' => true,
                'widget' => 'single_text',
                'data' => new \DateTime(),  // Cette ligne attribue la date actuelle par défaut
                'constraints' => [
                    new NotNull([
                        'message' => 'La date de don ne peut pas être nulle.',
                    ]),
                    new GreaterThanOrEqual([
                        'value' => new \DateTime('today'),
                        'message' => 'La date de remise doit être supérieure ou égale à aujourd\'hui.',
                    ]),
                ],
            ])
            
            
            
            
            ->add('Montant', NumberType::class, [
                'label' => 'Montant (optionnel)',
                'required' => false,
                'constraints' => [
                    new GreaterThan([
                        'value' => 0,
                        'message' => 'Le montant doit être supérieur à zéro.'
                    ]),
                ],
            ])
            ->add('chooseCampaign', CheckboxType::class, [
                'label' => 'Choose Campaign',
                'required' => false,
            ])
            ->add('campagne', EntityType::class, [
                'class' => 'App\Entity\Campagne', //the class path 
                'choice_label' => 'Titre', //  the property name 
                'placeholder' => 'Choose a Campagne', //  placeholder text
                'required' => false, // Make the field not required if you want to allow selecting no campagne
            ])
            ->add('submit', SubmitType::class, [
                'label' => 'Enregistrer',
            ]);
          
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Don::class,
        ]);
    }
}
