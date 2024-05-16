<?php

namespace App\Form;

use App\Entity\Categorie;
use App\Entity\Materiel;
use Doctrine\ORM\Mapping\Entity;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class MaterielType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('Libelle')
        ->add('Description',TextareaType::class)
        ->add('Disponibilite', ChoiceType::class, [
            'choices' => [
                'Available' => true,
                'Not available' => false,
            ],
            'expanded' => true,
        ])

        ->add('Prix')

        //->add('Image',FileType::class,array("data_class"=>null))
        ->add('Image', FileType::class, [
            'required' => false,
            'data_class' => null,
            'empty_data' => '',

        ])
        ->add('idCategorie', EntityType::class, [
            'class' => Categorie::class, 
            'choice_label' => 'libelle_categorie', 
        ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Materiel::class,
        ]);
    }
}
