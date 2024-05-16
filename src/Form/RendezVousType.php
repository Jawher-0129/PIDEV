<?php

namespace App\Form;

use App\Entity\Demande;
use App\Entity\RendezVous;

use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\DateTimeType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\Callback;
use Symfony\Component\Validator\Constraints\GreaterThanOrEqual;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\NotNull;
use Symfony\Component\Validator\Constraints\NotNullValidator;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Context\ExecutionContextInterface;

class RendezVousType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder 
            ->add('Date') 
             ->add('Date', DateTimeType::class, [
                'widget' => 'single_text',
                'data' => new \DateTime(),
                'constraints' => [
                    new GreaterThanOrEqual([
                        'value' => new \DateTime('today'),
                        'message' => 'La date de RendezVous doit être supérieure ou égale à aujourd\'hui.',
                    ]),
                ],
            ])
            ->add('Lieu', TextType::class, [
                'constraints' => [
                    new NotBlank(['message' => 'Vieullez remplir le champ de lieu']),
                    /* new Callback([$this, 'validateTitreCase']), */
                ],
            ])
            ->add('Objective', TextareaType::class, [
                'constraints' => [
                    new NotBlank(['message' => 'Vieullez remplir le champ de Objective ']),
                ],
            ])  
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => RendezVous::class,
        ]);
    }
   
}
