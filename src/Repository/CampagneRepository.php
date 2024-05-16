<?php

namespace App\Repository;

use App\Entity\Campagne;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Campagne>
 *
 * @method Campagne|null find($id, $lockMode = null, $lockVersion = null)
 * @method Campagne|null findOneBy(array $criteria, array $orderBy = null)
 * @method Campagne[]    findAll()
 * @method Campagne[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class CampagneRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Campagne::class);
    }

    public function findCampaignsByStartMonth()
    {
        // Récupère toutes les campagnes avec leurs dates de début
        $campagnes = $this->createQueryBuilder('c')
            ->select("c.Date_debut")
            ->getQuery()
            ->getResult();
    
        $monthlyCounts = [];
        foreach ($campagnes as $campagne) {
            // Formatte chaque date de début au format 'Y-m' (Année-Mois)
            $monthYear = $campagne['Date_debut']->format('Y-m');
            if (!isset($monthlyCounts[$monthYear])) {
                $monthlyCounts[$monthYear] = 0;
            }
            $monthlyCounts[$monthYear]++;
        }
    
        // Préparer les données pour le retour
        $result = [];
        foreach ($monthlyCounts as $monthYear => $count) {
            $result[] = ['monthYear' => $monthYear, 'count' => $count];
        }
     
        return $result;
    }
    
    // src/Repository/CampagneRepository.php

public function findBySearchTerm($searchTerm)
{
    $qb = $this->createQueryBuilder('c');

    if (!empty($searchTerm)) {
        $qb->where('c.Titre LIKE :searchTerm')
            ->orWhere('c.Description LIKE :searchTerm')
            ->setParameter('searchTerm', '%' . $searchTerm . '%');
    }

    return $qb->getQuery()->getResult();
}

    

//    /**
//     * @return Campagne[] Returns an array of Campagne objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('c')
//            ->andWhere('c.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('c.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Campagne
//    {
//        return $this->createQueryBuilder('c')
//            ->andWhere('c.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
