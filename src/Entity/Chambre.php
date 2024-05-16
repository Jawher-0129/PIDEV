<?php

namespace App\Entity;

use App\Repository\ChambreRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: ChambreRepository::class)]
class Chambre
{
    
    #[ORM\Id]
    #[ORM\Column]
    private ?int $Numero = null;

    #[ORM\Column]
    private ?bool $Disponibilite = null;

    #[ORM\Column]
    private ?int $Nombre_litsTotal = null;

    #[ORM\Column(nullable: true)]
    private ?int $Nmbr_litsDisponible = null;

    #[ORM\ManyToOne(inversedBy: 'chambre')]
    #[ORM\JoinColumn(name: 'personnel', referencedColumnName: 'id_personnel' ,nullable: false)]
    private ?Personnel $personnel = null;

    public function getNumero(): ?int
    {
        return $this->Numero;
    }

    public function setNumero(int $Numero): static
    {
        $this->Numero = $Numero;

        return $this;
    }

    public function isDisponibilite(): ?bool
    {
        return $this->Disponibilite;
    }

    public function setDisponibilite(bool $Disponibilite): static
    {
        $this->Disponibilite = $Disponibilite;

        return $this;
    }

    public function getNombreLitsTotal(): ?int
    {
        return $this->Nombre_litsTotal;
    }

    public function setNombreLitsTotal(int $Nombre_litsTotal): static
    {
        $this->Nombre_litsTotal = $Nombre_litsTotal;

        return $this;
    }

    public function getNmbrLitsDisponible(): ?int
    {
        return $this->Nmbr_litsDisponible;
    }

    public function setNmbrLitsDisponible(?int $Nmbr_litsDisponible): static
    {
        $this->Nmbr_litsDisponible = $Nmbr_litsDisponible;

        return $this;
    }

    public function getPersonnel(): ?Personnel
    {
        return $this->personnel;
    }

    public function setPersonnel($personnel):void 
    {
        $this->personnel = $personnel;
    }

    public function setChambre(?Personnel $personnel): static
    {
        $this->personnel = $personnel;

        return $this;
    }


    
}