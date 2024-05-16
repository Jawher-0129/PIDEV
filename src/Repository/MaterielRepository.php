<?php

namespace App\Repository;

use App\Entity\Materiel;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Materiel>
 *
 * @method Materiel|null find($id, $lockMode = null, $lockVersion = null)
 * @method Materiel|null findOneBy(array $criteria, array $orderBy = null)
 * @method Materiel[]    findAll()
 * @method Materiel[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class MaterielRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Materiel::class);
    }

//    /**
//     * @return Materiel[] Returns an array of Materiel objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('m')
//            ->andWhere('m.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('m.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Materiel
//    {
//        return $this->createQueryBuilder('m')
//            ->andWhere('m.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }

    public function triCroissant(): array
    {   
    $queryBuilder = $this->createQueryBuilder('m1')
        ->orderBy('m1.Prix', 'ASC');
    return $queryBuilder->getQuery()->getResult();
    }
    
    public function triDecroissant(): array
    {   
    $queryBuilder = $this->createQueryBuilder('m1')
        ->orderBy('m1.Prix', 'DESC');
    return $queryBuilder->getQuery()->getResult();
    }

    public function advancedSearch($id, $libelle)
    {
        $qb = $this->createQueryBuilder('m');

        if ($id) {
            $qb->andWhere('m.idMateriel = :id')
               ->setParameter('id', $id);
        }
    
        if ($libelle) {
            $qb->andWhere('m.Libelle LIKE :libelle')
               ->setParameter('libelle', '%' . $libelle . '%');
        }
    
        return $qb->getQuery()->getResult();
    }

    public function findByLibelle($query)
    {
        return $this->createQueryBuilder('e')
            ->andWhere('e.Libelle LIKE :query')
            ->setParameter('query', '%'.$query.'%')
            ->getQuery()
            ->getResult();
    }

    public function countByDisponibilite($dispo)
    {
        return $this->createQueryBuilder('r')
            ->select('COUNT(r.Disponibilite)')
            ->where('r.Disponibilite = :Disponibilite')
            ->setParameter('Disponibilite', $dispo)
            ->getQuery()
            ->getSingleScalarResult();
    }



}
