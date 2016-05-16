package com.agoraphilia.sql

import org.junit.Test

import collection.JavaConversions._
import java.sql.DriverManager

import com.agoraphilia.model.gen.Tables.PERSON
import com.agoraphilia.model.gen.tables.records.PersonRecord
import org.jooq._
import org.jooq.impl._
import org.jooq.impl.DSL._
import org.jooq.scala.Conversions._


class SqlTest {

  @Test
  def test(): Unit = {
    val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/agoraphilia", "postgres", "consensus")
    val db = DSL.using(connection, SQLDialect.POSTGRES_9_4)

    val person = new PersonRecord()
    person.setName("bob")
    db.executeInsert(person)
    println(person.getId)


    val p2 = db.newRecord(PERSON)
    p2.setName("bob")
    p2.store()
    p2.store()
    println(p2.getId)
    //    PERSON.newRecord()

    //    println(person.store())
    for {
      result <- db.select(PERSON.ID).from(PERSON).fetch().toVector
    } yield {
      println(result)
    }
  }
}
