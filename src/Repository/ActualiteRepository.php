<?php

namespace App\Repository;

use App\Entity\Actualite;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Actualite>
 *
 * @method Actualite|null find($id, $lockMode = null, $lockVersion = null)
 * @method Actualite|null findOneBy(array $criteria, array $orderBy = null)
 * @method Actualite[]    findAll()
 * @method Actualite[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ActualiteRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Actualite::class);
    }

//    /**
//     * @return Actualite[] Returns an array of Actualite objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('a')
//            ->andWhere('a.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('a.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Actualite
//    {
//        return $this->createQueryBuilder('a')
//            ->andWhere('a.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }

//stat
    // Count events by date
    public function countEventsByDate(): array
    {
        $events = $this->createQueryBuilder('e')
            ->select('e.Date')
            ->getQuery()
            ->getResult();

        $countByDate = [];
        foreach ($events as $event) {
            $date = $event['Date']->format('Y-m-d');
            if (!isset($countByDate[$date])) {
                $countByDate[$date] = 1;
            } else {
                $countByDate[$date]++;
            }
        }

        return $countByDate;
    }


// Count all actualites
public function countByActualites(): int
{
    return $this->createQueryBuilder('a')
        ->select('COUNT(a.id_actualite)')
        ->getQuery()
        ->getSingleScalarResult();
}
     
}
