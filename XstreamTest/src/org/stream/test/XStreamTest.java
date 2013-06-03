package org.stream.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stream.entity.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
 
/**
 * <b>function:</b>Java�����XML�ַ������໥ת��
 * jar-lib-version: xstream-1.3.1
 * @author hoojo
 * @createDate Nov 27, 2010 12:15:15 PM
 * @file XStreamTest.java
 * @package com.hoo.test
 * @project WebHttpUtils
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class XStreamTest {
    
    private XStream xstream = null;
    private ObjectOutputStream  out = null;
    private ObjectInputStream in = null;
    
    private Student bean = null;
    
    /**
     * <b>function:</b>��ʼ����Դ׼��
     * @author hoojo
     * @createDate Nov 27, 2010 12:16:28 PM
     */
    @Before
    public void init() {
        try {
            xstream = new XStream();
            //xstream = new XStream(new DomDriver()); // ��Ҫxpp3 jar
        } catch (Exception e) {
            e.printStackTrace();
        }
        bean = new Student();
        bean.setAddress("china");
        bean.setEmail("jack@email.com");
        bean.setId(1);
        bean.setName("jack");
        Birthday day = new Birthday();
        day.setBirthday("2010-11-22");
        bean.setBirthday(day);
    }
    
    /**
     * <b>function:</b>�ͷŶ�����Դ
     * @author hoojo
     * @createDate Nov 27, 2010 12:16:38 PM
     */
    @After
    public void destory() {
        xstream = null;
        bean = null;
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.gc();
    }
    
    public final void fail(String string) {
        System.out.println(string);
    }
    
    public final void failRed(String string) {
        System.err.println(string);
    }
    
    /**
     * <b>function:</b>Java����ת����XML�ַ���
     * @author hoojo
     * @createDate Nov 27, 2010 12:19:01 PM
     */
    @Test
    public void writeBean2XML() {
        try {
            fail("------------Bean->XML------------");
            fail(xstream.toXML(bean));
            fail("���������XML");
            //��������
            xstream.alias("account", Student.class);
            xstream.alias("����", Birthday.class);
            xstream.aliasField("����", Student.class, "birthday");
            xstream.aliasField("����", Birthday.class, "birthday");
            //fail(xstream.toXML(bean));
            //����������
            xstream.aliasField("�ʼ�", Student.class, "email");
            //��������
            xstream.aliasPackage("hoo", "com.hoo.entity");
            fail(xstream.toXML(bean));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    /**
     * ��List����ת����xml�ĵ�
     */
    public void writeList2XML() {
        try {
        	
            //�޸�Ԫ������
            xstream.alias("beans", ListBean.class);
            xstream.alias("student", Student.class);
            fail("----------List-->XML----------");
            ListBean listBean = new ListBean();
            listBean.setName("this is a List Collection");
            
            List<Object> list = new ArrayList<Object>();
            list.add(bean);
            list.add(bean);//����bean
            //list.add(listBean);//����listBean����Ԫ��
            
            bean = new Student();
            bean.setAddress("china");
            bean.setEmail("tom@125.com");
            bean.setId(2);
            bean.setName("tom");
            Birthday day = new Birthday("2010-11-22");
            bean.setBirthday(day);
            
            list.add(bean);
            listBean.setList(list);
            
            //��ListBean�еļ������ÿ�Ԫ�أ�������ʾ����Ԫ�ر�ǩ
            //xstream.addImplicitCollection(ListBean.class, "list");
            
            //����referenceģ��
            //xstream.setMode(XStream.NO_REFERENCES);//������
            // xstream.setMode(XStream.ID_REFERENCES);//id����
            xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);//����·������
//              
//            //��name����Ϊ���ࣨStudent����Ԫ�ص�����
              xstream.useAttributeFor(Student.class, "name");
              xstream.useAttributeFor(Birthday.class, "birthday");
              //�޸����Ե�name
              xstream.aliasAttribute("����", "name");
              xstream.aliasField("����", Birthday.class, "birthday");
            
            fail(xstream.toXML(listBean));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    /**
     * ��JavaBean�����Annotationע���������������
     */
    public void writeList2XML4Annotation() {
        try {
            failRed("---------annotation Bean --> XML---------");
            Student stu = new Student();
            stu.setName("jack");
            Classes c = new Classes("һ��", bean, stu);
            c.setNumber(2);
            //��ָ������ʹ��Annotation
            xstream.processAnnotations(Classes.class);
            //����Annotation
            xstream.autodetectAnnotations(true);
            xstream.alias("student", Student.class);
            fail(xstream.toXML(c));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    /**
     * 
     * Map����ת��xml�ĵ�
     */
    public void writeMap2XML() {
        try {
            failRed("---------Map --> XML---------");
            Map<String, Student> map = new HashMap<String, Student>();
            map.put("No.1", bean);//put
            
            bean = new Student();
            bean.setAddress("china");
            bean.setEmail("tom@125.com");
            bean.setId(2);
            bean.setName("tom");
            Birthday day = new Birthday("2010-11-22");
            bean.setBirthday(day);
            map.put("No.2", bean);//put
            
            bean = new Student();
            bean.setName("jack");
            map.put("No.3", bean);//put
            
            xstream.alias("student", Student.class);
            xstream.alias("key", String.class);
            xstream.useAttributeFor(Student.class, "id");
            xstream.useAttributeFor("birthday", String.class);
            fail(xstream.toXML(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    /**
     * ��OutStream�����дXML 
     */
    public void writeXML4OutStream() {
        try {
            out = xstream.createObjectOutputStream(System.out);
            Student stu = new Student();
            stu.setName("jack");
            Classes c = new Classes("һ��", bean, stu);
            c.setNumber(2);
            failRed("---------ObjectOutputStream # JavaObject--> XML---------");
            out.writeObject(stu);
            out.writeObject(new Birthday("2010-05-33"));
            out.write(22);//byte
            out.writeBoolean(true);
            out.writeFloat(22.f);
            out.writeUTF("hello");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    /**
     * ��InputStream��XML�ĵ�ת����java����
     */
    public void readXML4InputStream() {
        try {
            String s = "<object-stream><org.stream.entity.Student><id>0</id><name>jack</name>" +
              "</org.stream.entity.Student><org.stream.entity.Birthday><birthday>2010-05-33</birthday>" +
              "</org.stream.entity.Birthday><byte>22</byte><boolean>true</boolean><float>22.0</float>" +
              "<string>hello</string></object-stream>";
            failRed("---------ObjectInputStream## XML --> javaObject---------");
            StringReader reader = new StringReader(s);
            in = xstream.createObjectInputStream(reader);
            Student stu = (Student) in.readObject();
            Birthday b = (Birthday) in.readObject();
            byte i = in.readByte();
            boolean bo = in.readBoolean();
            float f = in.readFloat();
            String str = in.readUTF();
            System.out.println(stu);
            System.out.println(b);
            System.out.println(i);
            System.out.println(bo);
            System.out.println(f);
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void readXml2Object() {
        try {
            failRed("-----------Xml >>> Bean--------------");
            Student stu = (Student) xstream.fromXML(xstream.toXML(bean));
            fail("stu:"+stu.toString());
            
            List<Student> list = new ArrayList<Student>();
            list.add(bean);//add
            
            Map<String, Student> map = new HashMap<String, Student>();
            map.put("No.1", bean);//put
            
            bean = new Student();
            bean.setAddress("china");
            bean.setEmail("tom@125.com");
            bean.setId(2);
            bean.setName("tom");
            Birthday day = new Birthday("2010-11-22");
            bean.setBirthday(day);
            list.add(bean);//add
            map.put("No.2", bean);//put
            
            bean = new Student();
            bean.setName("jack");
            list.add(bean);//add
            map.put("No.3", bean);//put
            
            failRed("==========XML >>> List===========");
            List<Student> studetns = (List<Student>) xstream.fromXML(xstream.toXML(list));
            fail("size:" + studetns.size());//3
            for (Student s : studetns) {
                fail(s.toString());
            }
            
            failRed("==========XML >>> Map===========");
            Map<String, Student> maps = (Map<String, Student>) xstream.fromXML(xstream.toXML(map));
            fail("size:" + maps.size());//3
            Set<String> key = maps.keySet();
            Iterator<String> iter = key.iterator();
            while (iter.hasNext()) {
                String k = iter.next();
                fail(k + ":" + map.get(k));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    /**
     * ��JettisonMappedXmlDriver���Java����JSON��ת��
     */
    public void writeEntity2JETTSON() {
        failRed("=======JettisonMappedXmlDriver===JavaObject >>>> JaonString=========");
        xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("student", Student.class);
        fail(xstream.toXML(bean));
    }
    
    @Test
    /**
     * JsonHierarchicalStreamDriver���Java����JSON��ת��
     */
    public void writeEntiry2JSON() {
        fail("======JsonHierarchicalStreamDriver====JavaObject >>>> JaonString=========");
        xstream = new XStream(new JsonHierarchicalStreamDriver());
        //xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("student", Student.class);
        fail("-------Object >>>> JSON---------");
        fail(xstream.toXML(bean));
        
        fail("========JsonHierarchicalStreamDriver==ɾ�����ڵ�=========");
        //ɾ�����ڵ�
        xstream = new XStream(new JsonHierarchicalStreamDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE);
            }
        });
        //xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("student", Student.class);
        fail(xstream.toXML(bean));
    }
    
    @Test
    /**
     * ��List����ת����JSON�ַ���
     */
    public void writeList2JSON() {
        fail("======JsonHierarchicalStreamDriver====JavaObject >>>> JaonString=========");
        JsonHierarchicalStreamDriver driver = new JsonHierarchicalStreamDriver();
        xstream = new XStream(driver);
        //xstream = new XStream(new JettisonMappedXmlDriver());//ת������
        //xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("student", Student.class);
        
        List<Student> list = new ArrayList<Student>();
        list.add(bean);//add
        
        bean = new Student();
        bean.setAddress("china");
        bean.setEmail("tom@125.com");
        bean.setId(2);
        bean.setName("tom");
        Birthday day = new Birthday("2010-11-22");
        bean.setBirthday(day);
        list.add(bean);//add
        
        bean = new Student();
        bean.setName("jack");
        list.add(bean);//add
        
        fail(xstream.toXML(list));
        
        fail("========JsonHierarchicalStreamDriver==ɾ�����ڵ�=========");
        //ɾ�����ڵ�
        xstream = new XStream(new JsonHierarchicalStreamDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE);
            }
        });
        xstream.alias("student", Student.class);
        fail(xstream.toXML(list));
    }
    
    

    @Test
    /**
     * Mapת��json
     */
    public void writeMap2JSON() {
        failRed("======JsonHierarchicalStreamDriver==== Map >>>> JaonString=========");
        xstream = new XStream(new JsonHierarchicalStreamDriver());
        //xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.alias("student", Student.class);
        
        Map<String, Student> map = new HashMap<String, Student>();
        map.put("No.1", bean);//put
        
        bean = new Student();
        bean.setAddress("china");
        bean.setEmail("tom@125.com");
        bean.setId(2);
        bean.setName("tom");
        bean.setBirthday(new Birthday("2010-11-21"));
        map.put("No.2", bean);//put
        
        bean = new Student();
        bean.setName("jack");
        map.put("No.3", bean);//put
        
        fail(xstream.toXML(map));
        
        //failRed("========JsonHierarchicalStreamDriver==ɾ�����ڵ�=========");
        //ɾ�����ڵ�
        xstream = new XStream(new JsonHierarchicalStreamDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE);
            }
        });
        xstream.alias("student", Student.class);
        fail(xstream.toXML(map));
    }
    
    

    /**
     * <b>function:</b>JsonHierarchicalStreamDriver���Խ��򵥵�json�ַ���ת����java����list��mapת�����ɹ���
     * JsonHierarchicalStreamDriver��ȡJSON�ַ�����java�������
     * @author hoojo
     * @createDate Nov 27, 2010 1:22:26 PM
     * @throws JSONException
     * ��JSONת��java����
     */
    @Test
    public void readJSON2Object() throws JSONException {
        String json = "{\"student\": {" +
            "\"id\": 1," +
            "\"name\": \"haha\"," +
            "\"email\": \"email\"," +
            "\"address\": \"address\"," +
            "\"birthday\": {" +
                "\"birthday\": \"2010-11-22\"" +
            "}" +
        "}}";
        //JsonHierarchicalStreamDriver��ȡJSON�ַ�����java���������JettisonMappedXmlDriver����
        xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.alias("student", Student.class);
        fail("����"+xstream.fromXML(json).toString());
        
        //JettisonMappedXmlDriverת��List���ϳ�����JsonHierarchicalStreamDriver����ת����ȷ
        //JettisonMappedXmlDriver ת�����ַ��� {"list":{"student":[{"id":1,"name":"haha","email":"email","address":"address","birthday":[{},"2010-11-22"]}]},"student":{"id":2,"name":"tom","email":"tom@125.com","address":"china","birthday":[{},"2010-11-22"]}}
        json = "{\"list\": [{" +
                "\"id\": 1," +
                "\"name\": \"haha\"," +
                "\"email\": \"email\"," +
                "\"address\": \"address\"," +
                "\"birthday\": {" +
                  "\"birthday\": \"2010-11-22\"" +
                "}" +
               "},{" +
                "\"id\": 2," +
                "\"name\": \"tom\"," +
                "\"email\": \"tom@125.com\"," +
                "\"address\": \"china\"," +
                "\"birthday\": {" +
                  "\"birthday\": \"2010-11-22\"" +
                "}" +
              "}]}";
        System.out.println(json);//��jsת���ɹ�
       // xstream = new XStream(new JsonHierarchicalStreamDriver());
        List list = (List) xstream.fromXML(json);
        System.out.println(list.size());//0����ת��ʧ��
    }
}