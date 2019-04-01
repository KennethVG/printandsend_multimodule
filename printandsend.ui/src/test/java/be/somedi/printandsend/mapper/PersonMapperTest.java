package be.somedi.printandsend.mapper;

import be.somedi.printandsend.entity.PersonEntity;
import be.somedi.printandsend.model.medidoc.PersonMedidoc;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class PersonMapperTest {

    @Test
    public void testPersonEntityToPersonMedidoc(){
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName("Jos");
        personEntity.setBirthDate(LocalDate.now());
        personEntity.setLastName("Vermeulen");
        personEntity.setInss("88101322344");

        PersonMedidoc personMedidoc = PersonMapper.INSTANCE.personEntityToPersonMedidoc(personEntity);
        assertEquals(personEntity.getInss(), personMedidoc.getInss());
        assertEquals(personEntity.getBirthDate(), personMedidoc.getBirthDate());
        assertEquals("Vermeulen               ", personMedidoc.getLastName());
        assertEquals(personEntity.getFirstName(), personMedidoc.getFirstName());
    }
}