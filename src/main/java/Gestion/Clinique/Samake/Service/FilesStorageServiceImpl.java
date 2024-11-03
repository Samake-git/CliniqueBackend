package Gestion.Clinique.Samake.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    private final Path root = Paths.get("images_du_projet");

    @Override
    public void init() {
        try {
            Files.createDirectories(root); // Ensure directories are created
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }


    @Override
    public String save(MultipartFile file) {
        try {
            // Chemin complet du fichier
            Path filePath = this.root.resolve(file.getOriginalFilename());

            // Vérifier si le fichier existe déjà
            if (Files.exists(filePath)) {
                // Si le fichier existe, retourner l'URL existante
                return "http://localhost:8080/images/" + file.getOriginalFilename();
            }

            // Sinon, sauvegarder le fichier
            Files.copy(file.getInputStream(), filePath);
            // Retourne l'URL du fichier sauvegardé
            return "http://localhost:8080/images/" + file.getOriginalFilename();
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }




    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @PostConstruct
    public void run() {
       // this.deleteAll();
       //this.init();
    }

}