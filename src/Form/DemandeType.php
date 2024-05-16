<?php

namespace App\Form;

use App\Entity\Demande;
use App\Entity\Don;
use App\Entity\User1;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\Callback;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Context\ExecutionContextInterface;

class DemandeType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            //->add('Date')
            ->add('titre', TextType::class, [
                'attr' => ['class' => 'form-control bg-light border-0', 'placeholder' => 'Titre','style' => 'height: 55px;'],
                'constraints' => [
                    new NotBlank(['message' => 'Vieullez Entrer le champ de titre.']), 
                    /* new Callback([$this, 'validateTitreCase']), */ 
                ],
            ])
            //->add('titre')
            ->add('Description', TextareaType::class, [
                'attr' => ['class' => 'form-control bg-light border-0',  'placeholder' => 'description','style' => 'height: 100px;'],
                'constraints' => [
                    new NotBlank(['message' => 'Vieullez Entrer le champ de discription.']),
                ],
            ])
            ->add('Statut', null, [
                'attr' => ['readonly' => true],
            ])
            //->add('rendezVous')
          /*   ->add('don', EntityType::class, [
                'class' => Don ::class ,
                'choice_label' => 'id' ,
                'multiple' => false ,
                'expanded' => false ,
            ]) */
           // ->add('directeurCampagne')
        ;
    }
    public function validateTitreCase($value, ExecutionContextInterface $context): void
    {
        // Check if the value is not blank and not only whitespace before applying uppercase validation
        if (trim($value) !== '' && $value !== strtoupper($value)) {
            $context->buildViolation('The title must be in uppercase.')->addViolation();
        }
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Demande::class,
        ]);
    }
}
