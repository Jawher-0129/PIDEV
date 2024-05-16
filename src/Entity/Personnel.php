<?php

namespace App\Entity;

// src/Entity/Personnel.php

use App\Repository\PersonnelRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: PersonnelRepository::class)]
class Personnel
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id_personnel = null;

    #[ORM\Column(length: 50)]
    private ?string $Nom = null;

    #[ORM\Column(length: 50)]
    private ?string $Prenom_personnel = null;

    #[ORM\Column]
    private ?bool $Disponibilite = null;

    #[ORM\Column(length: 50)]
    private ?string $Role = null;

    #[ORM\OneToMany(targetEntity: Chambre::class, mappedBy: 'chambre', orphanRemoval: true)]
    private Collection $chambres; 

    #[ORM\Column]
    private ?int $experience = null;

    #[ORM\Column(type: "string", nullable: true)]
    private $image;

    #[ORM\Column(type: "integer", nullable: true)]
    private $rating;

    #[ORM\ManyToOne(inversedBy: 'personnels')]
    private ?User1 $UserId = null; // Add the rating property

    public function __construct()
    {
        $this->chambres = new ArrayCollection();
    }

    public function getIdPersonnel(): ?int
    {
        return $this->id_personnel;
    }

    public function getNom(): ?string
    {
        return $this->Nom;
    }

    public function setNom(string $Nom): self
    {
        $this->Nom = $Nom;

        return $this;
    }

    public function getPrenomPersonnel(): ?string
    {
        return $this->Prenom_personnel;
    }

    public function setPrenomPersonnel(string $Prenom_personnel): self
    {
        $this->Prenom_personnel = $Prenom_personnel;

        return $this;
    }

    public function isDisponibilite(): ?bool
    {
        return $this->Disponibilite;
    }

    public function setDisponibilite(bool $Disponibilite): self
    {
        $this->Disponibilite = $Disponibilite;

        return $this;
    }

    public function getRole(): ?string
    {
        return $this->Role;
    }

    public function setRole(string $Role): self
    {
        $this->Role = $Role;

        return $this;
    }

    /**
     * @return Collection<int, Chambre>
     */
    public function getChambre(): Collection
    {
        return $this->chambres;
    }

    public function addChambre(Chambre $chambre): self
    {
        if (!$this->chambres->contains($chambre)) {
            $this->chambres->add($chambre);
            $chambre->setChambre($this);
        }

        return $this;
    }

    public function removeChambre(Chambre $chambre): self
    {
        if ($this->chambres->removeElement($chambre)) {
            // set the owning side to null (unless already changed)
            if ($chambre->getPersonnel() === $this) {
                $chambre->setChambre(null);
            }
        }

        return $this;
    }

    public function __toString(): string
    {
        return $this->Nom . ' ' . $this->Prenom_personnel;
    }

    public function getExperience(): ?int
    {
        return $this->experience;
    }

    public function setExperience(int $experience): self
    {
        $this->experience = $experience;

        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): void
    {
        $this->image = $image;
    }

    public function getRating(): ?int
    {
        return $this->rating;
    }

    public function setRating(?int $rating): self
    {
        $this->rating = $rating;

        return $this;
    }

    public function getUserId(): ?User1
    {
        return $this->UserId;
    }

    public function setUserId(?User1 $UserId): static
    {
        $this->UserId = $UserId;

        return $this;
    }
}