package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if(usuarios.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        if(usuarioRepository.existsByEmail(usuario.getEmail()) ||
            usuarioRepository.existsByCpf(usuario.getCpf())){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario usuarioCriado = usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Usuario usuarioEncontrado = usuarioRepository.findById(id)
                                    .orElse(null);

        if(usuarioEncontrado == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuarioEncontrado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if(usuarioRepository.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam("nascimento") LocalDate teste) {
        List<Usuario> usuariosEncontrados = usuarioRepository.findByDataNascimentoGreaterThan(teste);

        if(usuariosEncontrados.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuariosEncontrados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario
    ) {

        Usuario usuarioEmailEncontrado = usuarioRepository.findByEmail(usuario.getEmail());

        Usuario usuarioCpfEncontrado = usuarioRepository.findByCpf(usuario.getCpf());

        if(usuarioEmailEncontrado != null &&
           !usuarioEmailEncontrado.getId().equals(id)
                ||
           usuarioCpfEncontrado != null &&
           !usuarioCpfEncontrado.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        usuario.setId(id);

        return ResponseEntity.ok(usuarioRepository.save(usuario));

    }
}
