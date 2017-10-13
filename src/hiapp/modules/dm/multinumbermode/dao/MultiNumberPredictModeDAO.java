package hiapp.modules.dm.multinumbermode.dao;

import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.utils.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MultiNumberPredictModeDAO extends BaseRepository {

    public List<MultiNumberCustomer> getAllActiveCustomers() {
        return null;
    }

}
