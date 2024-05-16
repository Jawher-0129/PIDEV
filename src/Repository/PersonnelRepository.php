<?php

namespace App\Repository;

use App\Entity\Personnel;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Doctrine\ORM\Query\Expr\Join;


/**
 * @extends ServiceEntityRepository<Personnel>
 *
 * @method Personnel|null find($id, $lockMode = null, $lockVersion = null)
 * @method Personnel|null findOneBy(array $criteria, array $orderBy = null)
 * @method Personnel[]    findAll()
 * @method Personnel[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class PersonnelRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Personnel::class);
    }

//    /**
//     * @return Personnel[] Returns an array of Personnel objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('p.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Personnel
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
// Method to search for Evenements based only on titre
// src/Repository/PersonnelRepository.php



    /**
     * @return Personnel[] Returns an array of Personnel objects
     */
    public function findByNomAndRole($nom, $role)
{
    $qb = $this->createQueryBuilder('p');

    // Check if $nom is not empty and add the condition
    if (!empty($nom)) {
        $qb->andWhere('p.Nom LIKE :nom')
            ->setParameter('nom', '%' . $nom . '%');
    }

    // Check if $role is not empty and add the condition
    if (!empty($role)) {
        $qb->andWhere('p.Role = :role')
            ->setParameter('role', $role);
    }

    return $qb->orderBy('p.id_personnel', 'ASC')
        ->getQuery()
        ->getResult();
}

}