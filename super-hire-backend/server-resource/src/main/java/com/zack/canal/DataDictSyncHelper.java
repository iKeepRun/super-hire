package com.zack.canal;

import com.zack.co.DataDictionaryCO;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@CanalTable(value = "data_dictionary")
@Component
public class DataDictSyncHelper implements EntryHandler<DataDictionaryCO> {

    @Override
    public void insert(DataDictionaryCO dataDictionaryCO) {
        System.out.println( "Insert detected: " + dataDictionaryCO);
    }

    @Override
    public void update(DataDictionaryCO before, DataDictionaryCO after) {
        System.out.println("Update detected: " + before + " -> " + after);
    }

    @Override
    public void delete(DataDictionaryCO dataDictionaryCO) {
        System.out.println("Delete detected: " + dataDictionaryCO);
    }
}
