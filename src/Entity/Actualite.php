<?php

namespace App\Entity;

use App\Repository\ActualiteRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: ActualiteRepository::class)]
class Actualite
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id_actualite= null;

    #[ORM\Column(length: 50)]
    private ?string $Titre = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $Description = null;

    #[ORM\Column(length: 55)]
    private ?string $Type_pub_cible = null;

    #[ORM\Column(length: 255)]
    private ?string $Theme = null;

    #[ORM\OneToOne(inversedBy: 'actualite', cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(name: 'evenement_id', referencedColumnName: 'id_evenement')]
    private ?Evenement $Evenement = null;

    #[ORM\ManyToOne(inversedBy: 'actualites')]
    private ?User1 $User = null;

    public function getId(): ?int
    {
        return $this->id_actualite;
    }

    public function getTitre(): ?string
    {
        return $this->Titre;
    }

    public function setTitre(string $Titre): static
    {
        $this->Titre = $Titre;

        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->Description;
    }

    public function setDescription(?string $Description): static
    {
        $this->Description = $Description;

        return $this;
    }

    public function getTypePubCible(): ?string
    {
        return $this->Type_pub_cible;
    }

    public function setTypePubCible(string $Type_pub_cible): static
    {
        $this->Type_pub_cible = $Type_pub_cible;

        return $this;
    }

    public function getTheme(): ?string
    {
        return $this->Theme;
    }

    public function setTheme(string $Theme): static
    {
        $this->Theme = $Theme;

        return $this;
    }

    public function getEvenement(): ?Evenement
    {
        return $this->Evenement;
    }

    public function setEvenement(?Evenement $Evenement): static
    {
        $this->Evenement = $Evenement;

        return $this;
    }

    public function getUser(): ?User1
    {
        return $this->User;
    }

    public function setUser(?User1 $User): static
    {
        $this->User = $User;

        return $this;
    }

}