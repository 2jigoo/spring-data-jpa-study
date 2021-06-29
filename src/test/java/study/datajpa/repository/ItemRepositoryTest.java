package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import study.datajpa.entity.Item;

@SpringBootTest
public class ItemRepositoryTest {
    
    @Autowired ItemRepository itemRepository;

    @Test
    public void save() {
        Item item = new Item("A");
        itemRepository.save(item);

        // PK가 String이고 이미 값이 있다면?
        // isNew(entity)가 false여서 merge하게 됨.
        // 이런 경우 Persistable이라는 인터페이스를 구현해 isNew()를 잘 오버라이딩 해주면 persist로 동작하게 된다.
    }
}
