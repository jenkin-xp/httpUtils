package cn.edu.xp.http;

import org.junit.Test;

/**
 * @Description 功能概述
 * @Author xp
 * @Date 2021/12/6 16:35
 * @Version V1.0
 **/
public class TestApplication {

    @Test
    public void testApi() {
        String s = HttpClient.get("https://www.baidu.com/sugrec?prod=pc_his&from=pc_web&json=1&sid=35292_35106_35014_34584_34505_35233_35331_35320_26350_35210_22157&hisdata=&_t=1638784313215&req=2&csor=0");
        System.out.println(s);
    }
    
}
