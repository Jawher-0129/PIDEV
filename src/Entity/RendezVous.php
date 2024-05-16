<?php

namespace App\Entity;

use App\Repository\RendezVousRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: RendezVousRepository::class)]
class RendezVous
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id_rendezvous = null;

    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $Date = null;

    #[ORM\Column(length: 255)]
    private ?string $Lieu = null;

    #[ORM\Column(length: 255)]
    private ?string $Objective = null;

    #[ORM\OneToOne(inversedBy: 'rendezVous', cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(name: 'demande', referencedColumnName: 'id_demande' ,nullable: false)]
   // #[ORM\JoinColumn(nullable: false)]
    private ?Demande $demande = null;

    public function getId_rendezvous(): ?int
    {
        return $this->id_rendezvous;
    }

    public function getDate(): ?\DateTimeInterface
    {
        return $this->Date;
    }

    public function setDate(\DateTimeInterface $Date): static
    {
        $this->Date = $Date;

        return $this;
    }

    public function getLieu(): ?string
    {
        return $this->Lieu;
    }

    public function setLieu(string $Lieu): static
    {
        $this->Lieu = $Lieu;

        return $this;
    }

    public function getObjective(): ?string
    {
        return $this->Objective;
    }

    public function setObjective(string $Objective): static
    {
        $this->Objective = $Objective;

        return $this;
    }

    public function getDemande(): ?Demande
    {
        return $this->demande;
    }

    public function setDemande(?Demande $demande): static
    {
        $this->demande = $demande;

        return $this;
    }
}
