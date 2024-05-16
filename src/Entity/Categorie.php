<?php

namespace App\Entity;

use App\Repository\CategorieRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: CategorieRepository::class)]
class Categorie
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $IdCategorie = null;

    #[ORM\Column(length: 255)]
    private ?string $LibelleCategorie = null;

    #[ORM\OneToMany(targetEntity: Materiel::class, mappedBy: 'idCategorie')]
    private Collection $materiels;

    #[ORM\ManyToOne(inversedBy: 'categories')]
    private ?User1 $User = null;

    public function __construct()
    {
        $this->materiels = new ArrayCollection();
    }

    public function getIdCategorie(): ?int
    {
        return $this->IdCategorie;
    }

    public function getLibelleCategorie(): ?string
    {
        return $this->LibelleCategorie;
    }

    public function setLibelleCategorie(string $LibelleCategorie): static
    {
        $this->LibelleCategorie = $LibelleCategorie;

        return $this;
    }

    /**
     * @return Collection<int, Materiel>
     */
    public function getMateriels(): Collection
    {
        return $this->materiels;
    }

    public function addMateriel(Materiel $materiel): static
    {
        if (!$this->materiels->contains($materiel)) {
            $this->materiels->add($materiel);
            $materiel->setIdCategorie($this);
        }

        return $this;
    }

    public function removeMateriel(Materiel $materiel): static
    {
        if ($this->materiels->removeElement($materiel)) {
            // set the owning side to null (unless already changed)
            if ($materiel->getIdCategorie() === $this) {
                $materiel->setIdCategorie(null);
            }
        }

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
