<?php

namespace App\Entity;

use App\Repository\DonRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: DonRepository::class)]
class Don
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 55)]
    private ?string $Type = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    private ?\DateTimeInterface $Date_remise = null;

    #[ORM\Column(nullable: true)]
    private ?int $Montant = null;

    #[ORM\ManyToOne(inversedBy: 'don')]
    private ?Campagne $campagne = null;

    #[ORM\ManyToOne(inversedBy: 'dons')]
    #[ORM\JoinColumn(name: 'Donateur', referencedColumnName: 'id' ,nullable: false)]
    private ?User1 $Donateur = null;

    // Transient field for form handling
    private ?bool $chooseCampaign = null;


    public function getId(): ?int
    {
        return $this->id;
    }

    public function getType(): ?string
    {
        return $this->Type;
    }

    public function setType(string $Type): static
    {
        $this->Type = $Type;

        return $this;
    }

    public function getDateRemise(): ?\DateTimeInterface
    {
        return $this->Date_remise;
    }

    public function setDateRemise(\DateTimeInterface $Date_remise): static
    {
        $this->Date_remise = $Date_remise;

        return $this;
    }

    public function getMontant(): ?int
    {
        return $this->Montant;
    }

    public function setMontant(?int $Montant): static
    {
        $this->Montant = $Montant;

        return $this;
    }

    public function getCampagne(): ?Campagne
    {
        return $this->campagne;
    }

    public function setCampagne(?Campagne $campagne): static
    {
        $this->campagne = $campagne;

        return $this;
    }

    public function getDonateur(): ?User1
    {
        return $this->Donateur;
    }

    public function setDonateur(?User1 $Donateur): static
    {
        $this->Donateur = $Donateur;

        return $this;
    }

    // Transient getter for chooseCampaign
    public function getChooseCampaign(): ?bool
    {
        return $this->chooseCampaign;
    }

    // Transient setter for chooseCampaign
    public function setChooseCampaign(?bool $chooseCampaign): void
    {
        $this->chooseCampaign = $chooseCampaign;
    }
    public function __toString()
    {
        return $this->id;
    }
}
