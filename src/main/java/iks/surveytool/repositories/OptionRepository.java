package iks.surveytool.repositories;

import iks.surveytool.entities.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
}
