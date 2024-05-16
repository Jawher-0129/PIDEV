<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240302155411 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE campagne ADD directeur_id INT DEFAULT NULL');
        $this->addSql('ALTER TABLE campagne ADD CONSTRAINT FK_539B5D16E82E7EE8 FOREIGN KEY (directeur_id) REFERENCES user1 (id)');
        $this->addSql('CREATE INDEX IDX_539B5D16E82E7EE8 ON campagne (directeur_id)');
        $this->addSql('ALTER TABLE materiel DROP FOREIGN KEY FK_18D2B091C9486A13');
        $this->addSql('ALTER TABLE materiel DROP FOREIGN KEY FK_18D2B09179F37AE5');
        $this->addSql('ALTER TABLE materiel ADD CONSTRAINT FK_18D2B091C9486A13 FOREIGN KEY (id_categorie) REFERENCES categorie (id_categorie)');
        $this->addSql('ALTER TABLE materiel ADD CONSTRAINT FK_18D2B09179F37AE5 FOREIGN KEY (id_user_id) REFERENCES user1 (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE campagne DROP FOREIGN KEY FK_539B5D16E82E7EE8');
        $this->addSql('DROP INDEX IDX_539B5D16E82E7EE8 ON campagne');
        $this->addSql('ALTER TABLE campagne DROP directeur_id');
        $this->addSql('ALTER TABLE materiel DROP FOREIGN KEY FK_18D2B091C9486A13');
        $this->addSql('ALTER TABLE materiel DROP FOREIGN KEY FK_18D2B09179F37AE5');
        $this->addSql('ALTER TABLE materiel ADD CONSTRAINT FK_18D2B091C9486A13 FOREIGN KEY (id_categorie) REFERENCES categorie (id_categorie) ON UPDATE CASCADE ON DELETE CASCADE');
        $this->addSql('ALTER TABLE materiel ADD CONSTRAINT FK_18D2B09179F37AE5 FOREIGN KEY (id_user_id) REFERENCES user1 (id) ON UPDATE CASCADE ON DELETE CASCADE');
    }
}
