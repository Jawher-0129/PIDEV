<?php

namespace App\Form;

use App\Entity\User1;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Security\Core\Authorization\AuthorizationCheckerInterface;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Validator\Constraints as Assert;

class UpdateType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        
        $builder
        ->add('update', SubmitType::class)
        ->add('nom', TextType::class, [
            'constraints' => [
                new Assert\NotBlank(['message' => 'Veuillez saisir votre nom.']),
                new Assert\Length([
                    'min' => 2,
                    'max' => 10,
                    'minMessage' => 'Le nom doit comporter au moins {{ limit }} caractères.',
                    'maxMessage' => 'Le nom ne peut pas dépasser {{ limit }} caractères.',
                ]),
            ],
            'attr' => [
                'class' => 'form-control',
            ],
        ])
        ->add('prenom', TextType::class, [
            'constraints' => [
                new Assert\NotBlank(['message' => 'Veuillez saisir votre prénom.']),
                new Assert\Length([
                    'min' => 2,
                    'max' => 10,
                    'minMessage' => 'Le prénom doit comporter au moins {{ limit }} caractères.',
                    'maxMessage' => 'Le prénom ne peut pas dépasser {{ limit }} caractères.',
                ]),
            ],
            'attr' => [
                'class' => 'form-control',
            ],
        ])
        ->add('email', TextType::class, [
            'constraints' => [
                new Assert\Email(['message' => "L'adresse email '{{ value }}' n'est pas valide."]),
                new Assert\NotBlank(['message' => 'Veuillez saisir votre adresse email.']),
            ],
            'attr' => [
                'class' => 'form-control',
            ],
        ])
        ->add('password', PasswordType::class, [
            'constraints' => [
                new Assert\NotBlank(['message' => 'Veuillez saisir votre mot de passe.']),
                new Assert\Length([
                    'min' => 6,
                    'minMessage' => 'Votre mot de passe doit comporter au moins {{ limit }} caractères.',
                ]),
                new Assert\Regex([
                    'pattern' => "/^(?=.*[A-Za-z])(?=.*\d).+$/",
                    'message' => 'Votre mot de passe doit contenir au moins une lettre et un chiffre.',
                ]),
            ],
            'attr' => [
                'class' => 'form-control',
            ],
        ])
        ->add('adresse', TextType::class, [
            'constraints' => [
                new Assert\NotBlank(['message' => 'Veuillez saisir votre adresse.']),
                new Assert\Length([
                    'min' => 2,
                    'max' => 20,
                    'minMessage' => 'L\'adresse doit comporter au moins {{ limit }} caractères.',
                    'maxMessage' => 'L\'adresse ne peut pas dépasser {{ limit }} caractères.',
                ]),
            ],
            'attr' => [
                'class' => 'form-control',
            ],
        ])
        ->add('telephone', TextType::class, [
            'constraints' => [
                new Assert\NotBlank(['message' => 'Veuillez saisir votre numero de telephone.']),
                new Assert\Length([
                    'min' => 8,
                    'max' => 8,
                    'minMessage' => 'L\'num doit comporter au moins {{ limit }} caractères.',
                    'maxMessage' => 'L\'num ne peut pas dépasser {{ limit }} caractères.',
                ]),
            ],
            'attr' => [
                'class' => 'form-control',
            ],
        ]);



    }
    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => User1::class,
        ]);
    }
}
