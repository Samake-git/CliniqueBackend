package Gestion.Clinique.Samake.Repository;



import Gestion.Clinique.Samake.Model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    FileInfo findByName(String name);
}
