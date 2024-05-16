<?php

namespace App\Entity;

use App\Repository\MaterielRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: MaterielRepository::class)]
class Materiel
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $idMateriel = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Libelle ne doit pas etre vide")]
    #[Assert\Length(min:8,minMessage:"Libelle doit au moins contenir 8 caracteres")]
    private ?string $Libelle = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "S'il vous plait ecrire une description")]
    #[Assert\Length(min:12,minMessage:"Vous devez écrire un texte")]
    private ?string $Description = null;

    #[ORM\Column]
    private ?int $Disponibilite = null;

    #[ORM\Column(length: 255)]
    #[Assert\Image(mimeTypes: ["image/jpeg", "image/png"], mimeTypesMessage: "S'il vous plait télecharger une image valide JPEG ou PNG")]
    private ?string $Image = null;

    #[ORM\Column]
    #[Assert\NotBlank(message: "Le prix est important")]
    #[Assert\GreaterThan(value: 0, message: "Prix doit etre supérieur à 0")]
    private ?float $Prix = null;

    #[ORM\ManyToOne(inversedBy: 'materiels')]
    #[ORM\JoinColumn(name: 'id_categorie', referencedColumnName: 'id_categorie')]
    private ?Categorie $idCategorie = null;

    #[ORM\ManyToOne(inversedBy: 'materiels')]
    private ?User1 $IdUser = null;

    public function getidMateriel(): ?int
    {
        return $this->idMateriel;
    }

    public function getLibelle(): ?string
    {
        return $this->Libelle;
    }

    public function setLibelle(string $Libelle): static
    {
        $this->Libelle = $Libelle;

        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->Description;
    }

    public function setDescription(string $Description): static
    {
        $this->Description = $Description;

        return $this;
    }

    public function getDisponibilite(): ?int
    {
        return $this->Disponibilite;
    }

    public function setDisponibilite(int $Disponibilite): static
    {
        $this->Disponibilite = $Disponibilite;

        return $this;
    }

    public function getImage(): ?string
    {
        return $this->Image;
    }

    public function setImage(?string $Image): self
    {
        $this->Image = $Image;

        return $this;
    }

    public function getPrix(): ?float
    {
        return $this->Prix;
    }

    public function setPrix(float $Prix): static
    {
        $this->Prix = $Prix;

        return $this;
    }

    public function getIdCategorie(): ?Categorie
    {
        return $this->idCategorie;
    }

    public function setIdCategorie(?Categorie $idCategorie): static
    {
        $this->idCategorie = $idCategorie;

        return $this;
    }

    public function getIdUser(): ?User1
    {
        return $this->IdUser;
    }

    public function setIdUser(?User1 $IdUser): static
    {
        $this->IdUser = $IdUser;

        return $this;
    }

    public function getCategorieLibelle(): ?string
    {
    return $this->idCategorie ? $this->idCategorie->getLibelleCategorie() : null;
    }

    
}
