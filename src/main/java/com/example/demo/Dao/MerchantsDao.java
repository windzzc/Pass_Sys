package com.example.demo.Dao;

import com.example.demo.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantsDao extends JpaRepository<Merchants,Integer> {
        Merchants findById(Integer id);
        Merchants findByName(String Name);

        List<Merchants> findIdByIn(List<Integer> ids);

}
