<?php
namespace App\Form;
use App\Entity\Campagne;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormError;
use Symfony\Component\Form\FormEvent;
use Symfony\Component\Form\FormEvents;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\File;
use Symfony\Component\Validator\Constraints\GreaterThanOrEqual;
use Symfony\Component\Validator\Constraints\NotNull;

class CampagneType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('Image', FileType::class, [
            'data_class' => null,
            'attr' => [
                'class' => 'custom-file-input', // Add your desired CSS class here
            ],
            'constraints' => [
                new File([
                    'maxSize' => '1024k', // Use '1048576' bytes for 1MB instead
                    'mimeTypes' => [
                        'image/gif',
                        'image/jpeg',
                        'image/png',
                        'image/jpg', // 'image/jpg' is redundant with 'image/jpeg'
                    ],
            'mimeTypesMessage' => 'Please upload a valid image',
            
                ]),
                new NotNull([
                    'message' => 'L imagene peut pas être nulle.',
                ])
            ]
                ])
                
                ->add('Titre', null, [
                    'constraints' => [
                        new NotNull([
                            'message' => 'Le titre ne peut pas être nul.',
                        ]),
                    ],
                ])

            ->add('Description', null, [
                'constraints' => [
                    new NotNull([
                        'message' => 'La description ne peut pas être nulle.',
                    ]),
                ],
            ])
            ->add('Date_debut', DateType::class, [
                'widget' => 'single_text',
                'data' => new \DateTime(),  // Cette ligne attribue la date actuelle par défaut
                'constraints' => [
                    new GreaterThanOrEqual([
                        'value' => new \DateTime('today'),
                        'message' => 'La date de début doit être supérieure ou égale à aujourd\'hui.',
                    ]),
                ],
            ])
            ->add('Date_fin', DateType::class, [
                'widget' => 'single_text',
            ]);
        ;
        $builder->addEventListener(FormEvents::SUBMIT, function (FormEvent $event) {
            $form = $event->getForm();
            $data = $event->getData();

            //  getDateDebut() et getDateFin() 
            $startDate = $data->getDateDebut();
            $endDate = $data->getDateFin();

           //logique de validation
            if ($endDate < $startDate) {
                $form->get('Date_fin')->addError(new FormError('La date de fin doit etre supérieure à la date du début'));
            }
        });
    }
    
    

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Campagne::class,
        ]);
    }
}
