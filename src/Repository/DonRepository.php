<?php

namespace App\Repository;

use App\Entity\Don;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\ORM\Query\ResultSetMapping;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Security\Core\User\UserInterface;

/**
 * @extends ServiceEntityRepository<Don>
 *
 * @method Don|null find($id, $lockMode = null, $lockVersion = null)
 * @method Don|null findOneBy(array $criteria, array $orderBy = null)
 * @method Don[]    findAll()
 * @method Don[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class DonRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Don::class);
    }


// DonRepository.php

public function findDonsByCampagne($campagneId)
{
    return $this->createQueryBuilder('don')
        ->andWhere('don.campagne = :campagneId')
        ->setParameter('campagneId', $campagneId)
        ->getQuery()
        ->getResult();
}

 // New method to get statistics for a campaign
 public function getDonStatisticsByCampagne($campagneId)
 {
     // Use ResultSetMapping to execute native SQL query
     $rsm = new ResultSetMapping();
     $rsm->addScalarResult('totalDonations', 'totalDonations');

     $nativeQuery = $this->getEntityManager()->createNativeQuery('
         SELECT COUNT(id) as totalDonations
         FROM don
         WHERE campagne_id = :campagneId
     ', $rsm);

     $nativeQuery->setParameter('campagneId', $campagneId);

     return $nativeQuery->getSingleScalarResult();
 }

 

public function getDonationChartDataByCampagne($campagneId)
{
    // Implement the logic to fetch the data needed for the chart
    // Example: Assuming you have a 'createdAt' field in Don entity
    $data = $this->createQueryBuilder('don')
        ->select("DATE_FORMAT(don.Date_debut, '%M') as month", 'SUM(don.Montant) as totalAmount')
        ->where('don.campagne = :campagneId')
        ->setParameter('campagneId', $campagneId)
        ->groupBy('month')
        ->getQuery()
        ->getResult();

    return $data;
}

// CHart les dons par mois 

public function getDonationChartDataOverall()
{
    // Récupère toutes les données nécessaires sans grouper par mois dans la requête
    $dons = $this->createQueryBuilder('don')
        ->select("don.Date_remise")
        ->getQuery()
        ->getResult();

    $monthlyCounts = [];
    foreach ($dons as $don) {
        // Formatte chaque date de remise au format 'Y-m' (Année-Mois)
        $monthYear = $don['Date_remise']->format('Y-m');
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

//fonction pour le Tri front_office 

public function findUniqueDonTypes()
{
    return $this->createQueryBuilder('d')
        ->select('DISTINCT d.Type')
        ->getQuery()
        ->getResult();
}


public function findBySearchTerm($searchTerm)
{
    $qb = $this->createQueryBuilder('d')
        ->leftJoin('d.campagne', 'c'); // Joindre l'entité Campagne avec l'alias 'c'

    if (!empty($searchTerm)) {
        // Utilisez le bon nom d'attribut, respectez la casse
        $qb->where('d.Type LIKE :searchTerm') 
            ->orWhere('d.Montant LIKE :searchTerm')
            ->orWhere('d.Date_remise LIKE :searchTerm')
            ->orWhere('c.Titre LIKE :searchTerm') // Supposons que 'Titre' est un champ de 'Campagne'
            ->setParameter('searchTerm', '%' . $searchTerm . '%');
    }

    return $qb->getQuery()->getResult();
}

public function findByLimitOffset($limit, $offset = 0)
{
    return $this->createQueryBuilder('d')
        ->setMaxResults($limit)
        ->setFirstResult($offset)
        ->getQuery()
        ->getResult();
}

public function findByDonateurId($donateurId, $typeDon = null, $searchTerm = null, $limit = null, $offset = null)
    {
        $qb = $this->createQueryBuilder('d')
            ->where('d.donateur = :donateurId')
            ->setParameter('donateurId', $donateurId)
            ->leftJoin('d.campagne', 'c');

        if (!empty($typeDon)) {
            $qb->andWhere('d.Type = :typeDon')
               ->setParameter('typeDon', $typeDon);
        }

        if (!empty($searchTerm)) {
            $qb->andWhere('d.Type LIKE :searchTerm OR d.Montant LIKE :searchTerm OR d.Date_remise LIKE :searchTerm OR c.Titre LIKE :searchTerm')
               ->setParameter('searchTerm', '%' . $searchTerm . '%');
        }

        if (!is_null($limit)) {
            $qb->setMaxResults($limit);
        }

        if (!is_null($offset)) {
            $qb->setFirstResult($offset);
        }

        return $qb->getQuery()->getResult();
    }

    public function findByUser(UserInterface $user, $typeDon = null, $searchTerm = null, $limit = 6, $offset = 0): array
{
    $qb = $this->createQueryBuilder('d')
               ->where('d.Donateur = :user')
               ->leftJoin('d.campagne', 'c')
               ->setParameter('user', $user)
               ->setFirstResult($offset)
               ->setMaxResults($limit);

    if (!empty($typeDon)) {
        $qb->andWhere('d.Type = :typeDon')
           ->setParameter('typeDon', $typeDon);
    }

    if (!empty($searchTerm)) {
        $qb->andWhere('d.Type LIKE :searchTerm OR d.Montant LIKE :searchTerm OR d.Date_remise LIKE :searchTerm OR c.Titre LIKE :searchTerm')
           ->setParameter('searchTerm', '%' . $searchTerm . '%');
    }

    return $qb->getQuery()->getResult();
}





//    /**
//     * @return Don[] Returns an array of Don objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('d')
//            ->andWhere('d.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('d.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Don
//    {
//        return $this->createQueryBuilder('d')
//            ->andWhere('d.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
