package com.gk.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.reggie.entity.AddressBook;
import com.gk.reggie.mapper.AddressBookMapper;
import com.gk.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Service
public class AddressBookServiceImpl  extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
