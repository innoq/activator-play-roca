package com.innoq.rocaplay.domain

import org.joda.time.DateTime

package object issues {

  case class Issue(
    id: Long,
    projectName: Option[String],
    priority: Option[String],
    issueType: Option[String],
    summary: String,
    exceptionStackTrace: Option[String],
    description: Option[String],
    reporter: String,
    componentName: Option[String],
    componentVersion: Option[String],
    processingState: Option[String],
    openDate: DateTime,
    closeDate: Option[DateTime],
    closeAction: Option[String],
    assignee: Option[String],
    comment: Option[String])

  object Issue {
    def apply(
      projectName: Option[String],
      priority: Option[String],
      issueType: Option[String],
      summary: String,
      exceptionStackTrace: Option[String],
      description: Option[String],
      reporter: String,
      componentName: Option[String],
      componentVersion: Option[String],
      processingState: Option[String],
      openDate: DateTime,
      closeDate: Option[DateTime],
      closeAction: Option[String],
      assignee: Option[String],
      comment: Option[String]): Issue = Issue(
        System.currentTimeMillis(), projectName, priority, issueType, summary, exceptionStackTrace,
        description, reporter, componentName, componentVersion, processingState, openDate,
        closeDate, closeAction, assignee, comment
    )
  }

}
