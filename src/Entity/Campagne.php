<?php

namespace App\Entity;

use App\Repository\CampagneRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: CampagneRepository::class)]
class Campagne
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 55)]
    private ?string $Titre = null;

    #[ORM\Column(length: 255)]
    private ?string $Image = null;

    #[ORM\Column(length: 255)]
    private ?string $Description = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    private ?\DateTimeInterface $Date_debut = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    private ?\DateTimeInterface $Date_fin = null;

    #[ORM\OneToMany(targetEntity: Don::class, mappedBy: 'campagne')]
    private Collection $don;

    #[ORM\ManyToOne(inversedBy: 'campagnes')]
    private ?User1 $Directeur = null;

    public function __construct()
    {
        $this->don = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
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

    public function getImage(): ?string
    {
        return $this->Image;
    }

    public function setImage(string $Image): static
    {
        $this->Image = $Image;

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

    public function getDateDebut(): ?\DateTimeInterface
    {
        return $this->Date_debut;
    }

    public function setDateDebut(\DateTimeInterface $Date_debut): static
    {
        $this->Date_debut = $Date_debut;

        return $this;
    }

    public function getDateFin(): ?\DateTimeInterface
    {
        return $this->Date_fin;
    }

    public function setDateFin(\DateTimeInterface $Date_fin): static
    {
        $this->Date_fin = $Date_fin;

        return $this;
    }

    /**
     * @return Collection<int, Don>
     */
    public function getDon(): Collection
    {
        return $this->don;
    }

    public function addDon(Don $don): static
    {
        if (!$this->don->contains($don)) {
            $this->don->add($don);
            $don->setCampagne($this);
        }

        return $this;
    }

    public function removeDon(Don $don): static
    {
        if ($this->don->removeElement($don)) {
            // set the owning side to null (unless already changed)
            if ($don->getCampagne() === $this) {
                $don->setCampagne(null);
            }
        }

        return $this;
    }

    public function getDirecteur(): ?User1
    {
        return $this->Directeur;
    }

    public function setDirecteur(?User1 $Directeur): static
    {
        $this->Directeur = $Directeur;

        return $this;
    }
}
