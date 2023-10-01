package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.ImportedPersonDTO;
import com.example.taskmanager.dto.ImportedTaskDTO;
import com.example.taskmanager.service.ImportService;
import com.example.taskmanager.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    private final PersonService personService;

    @Override
    @SneakyThrows
    @Transactional(propagation = Propagation.REQUIRED)
    public void importFromFile(MultipartFile file)  {
        String content = new String(file.getBytes());
        content = content.replaceAll("\r","");
        content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + content;
        content= Arrays.stream(content.split("\n")).map(string->string.trim()).collect(Collectors.joining(""));
        InputStream stream = new ByteArrayInputStream(content.getBytes());

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(stream);
        doc.getDocumentElement().normalize();

        Element rootElement = doc.getDocumentElement();
        NodeList users = rootElement.getChildNodes();
        int length = users.getLength();

        Map<Integer,Long> bossesId = new HashMap<>();
        //проход первый - создаём и обновляем боссов. Боссы в мапу: номер ноды - id босса
        log.info("STARTING BOSS PROCESSING");
        for (int i=0; i<length; i++) {
            Node user = users.item(i);
            if (isBoss(user)) {
                log.info("{} is a boss",i);

                ImportedPersonDTO importedPersonDTO = parsePerson(user);
                //TODO проверить что у босов нет своих тасок и не стоит босс айди
                Long id;
                if (importedPersonDTO.getId()==null) {
                    id = personService.create(importedPersonDTO);
                } else {
                    id = importedPersonDTO.getId();
                    personService.update(importedPersonDTO);
                }
                bossesId.put(i,id);
                log.info("parsed boss {}",importedPersonDTO);
            }
        }

        //проход второй - создаём и обновляем сотрудников. если это вложенные у босса - id босса достаём из мапы по номеру ноды
        List<ImportedPersonDTO> employee = new ArrayList<>();
        log.info("STARTING EMPLOYEE PROCESSING");
        for (int i=0; i<length; i++) {
            Node user = users.item(i);
            if (isBoss(user)) {
                NodeList userFields = user.getChildNodes();
                for (int j=0; j<userFields.getLength(); j++) {
                    if (userFields.item(j).getNodeName().equals("users")) {
                        NodeList bossesUsers = userFields.item(j).getChildNodes();
                        for (int n = 0; n < bossesUsers.getLength(); n++) {
                            ImportedPersonDTO importedPersonDTO = parsePerson(bossesUsers.item(n));
                            //TODO возможно тут проверять что у них bossId не задано, иначе ошибка
                            importedPersonDTO.setBossId(bossesId.get(i));
                            employee.add(importedPersonDTO);
                            log.info("parsed bosses employee {}",importedPersonDTO);
                        }
                    }
                }
            } else {
                log.info("{} is a employee",i);
                ImportedPersonDTO importedPersonDTO = parsePerson(user);
                employee.add(importedPersonDTO);
                log.info("parsed employee {}",importedPersonDTO);
            }
        }
        for (ImportedPersonDTO employer: employee) {
            if (employer.getId()==null) {
                personService.create(employer);
            } else {
                personService.update(employer);
            }
        }
    }

    private ImportedPersonDTO parsePerson(Node user) {
        ImportedPersonDTO importedPersonDTO = new ImportedPersonDTO();
        NodeList userFields = user.getChildNodes();
        for (int i=0; i<userFields.getLength(); i++) {
            Node node = userFields.item(i);
            String nodeName = node.getNodeName();
            switch (nodeName) {     //TODO Вот тут рефлексия может одной строкой заменить свичи - получать поле по имени и сетить его
                case "userId":
                    importedPersonDTO.setId(Long.parseLong(node.getFirstChild().getNodeValue()));
                    break;
                case "name":
                    importedPersonDTO.setName(node.getFirstChild().getNodeValue());
                    break;
                case "boss":
                    importedPersonDTO.setBossId(Long.parseLong(node.getFirstChild().getNodeValue()));
                    break;
                case "tasks":
                    importedPersonDTO.setTasks(getTasks(node));
                    break;
                case "users":
                    break;
                default:
                    log.warn("Unknown node: {}",nodeName);
            }
        }

        return importedPersonDTO;
    }

    private List<ImportedTaskDTO> getTasks(Node node) {
        List<ImportedTaskDTO> tasks = new ArrayList<>();
        NodeList taskNodes = node.getChildNodes();
        for (int i=0; i<taskNodes.getLength(); i++) {
            ImportedTaskDTO importedTaskDTO = new ImportedTaskDTO();
            Node taskNode = taskNodes.item(i);
            NodeList taskFields = taskNode.getChildNodes();
            for (int j=0; j<taskFields.getLength(); j++) {
                Node field = taskFields.item(j);
                String nodeName = field.getNodeName();
                switch (nodeName) {     //TODO Вот тут тоже рефлексия может заменить свичи
                    case "name" -> importedTaskDTO.setName(field.getFirstChild().getNodeValue());
                    case "description" -> importedTaskDTO.setDescription(field.getFirstChild().getNodeValue());
                    case "expiredDate" -> {
                        String expiredDateAsString = field.getFirstChild().getNodeValue();
                        LocalDate date = LocalDate.parse(expiredDateAsString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        importedTaskDTO.setExpiredDate(date);
                    }
                    default -> log.warn("Unknown node: {}", nodeName);
                }
            }
            tasks.add(importedTaskDTO);
        }
        return tasks;
    }

    private boolean isBoss(Node user) {
        NodeList userFields = user.getChildNodes();
        for (int i=0; i<userFields.getLength(); i++) {
            if (userFields.item(i).getNodeName().equals("users")) {
                return true;
            }
        }
        return false;
    }
}
