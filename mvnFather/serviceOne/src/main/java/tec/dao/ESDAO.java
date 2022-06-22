package tec.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import tec.pojo.ESTest;

import java.util.List;

@Repository("esdao")
public interface ESDAO extends ElasticsearchRepository<ESTest, Long> {
    List<ESTest> findByName(String name);
    List<ESTest> findByAge(String name);
    List<ESTest> findBySex(String name);
    List<ESTest> findByAddress(String name);
}
