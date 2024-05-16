<?php

namespace App\Repository;

use App\Entity\Evenement;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Evenement>
 *
 * @method Evenement|null find($id, $lockMode = null, $lockVersion = null)
 * @method Evenement|null findOneBy(array $criteria, array $orderBy = null)
 * @method Evenement[]    findAll()
 * @method Evenement[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class EvenementRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Evenement::class);
    }

//    /**
//     * @return Evenement[] Returns an array of Evenement objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('e')
//            ->andWhere('e.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('e.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Evenement
//    {
//        return $this->createQueryBuilder('e')
//            ->andWhere('e.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }

public function findByTitre($query)
{
    return $this->createQueryBuilder('e')
        ->andWhere('e.Titre LIKE :query')
        ->setParameter('query', '%'.$query.'%')
        ->getQuery()
        ->getResult();
}
//trie date croi ou decr
public function findAllSortedByDateAsc(): array
{
    return $this->createQueryBuilder('e')
        ->orderBy('e.Date', 'ASC')
        ->getQuery()
        ->getResult();
}

public function findAllSortedByDateDesc(): array
{
    return $this->createQueryBuilder('e')
        ->orderBy('e.Date', 'DESC')
        ->getQuery()
        ->getResult();
}
//stat
public function countActualitesByTheme(): array
{
    return $this->createQueryBuilder('a')
        ->select('COUNT(a.id_actualite) as actualite_count', 'a.Theme as theme')
        ->groupBy('theme')
        ->getQuery()
        ->getResult();
}

   public function countAllEvents(): int
{
    return $this->createQueryBuilder('e')
        ->select('COUNT(e.id_evenement)')
        ->getQuery()
        ->getSingleScalarResult();
}
}
