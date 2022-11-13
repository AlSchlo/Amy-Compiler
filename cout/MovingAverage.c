#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include "std.h"
#include "error.h"

struct L_Nil {
  int32_t constr_index;
};

struct L_Cons {
  int32_t constr_index;
  int32_t att0;
  void* att1;
};

struct L_LP {
  int32_t constr_index;
  void* att0;
  void* att1;
};

struct O_None {
  int32_t constr_index;
};

struct O_Some {
  int32_t constr_index;
  int32_t att0;
};

int32_t L_isEmpty(void* l);
int32_t L_length(void* l);
int32_t L_head(void* l);
void* L_headOption(void* l);
void* L_reverse(void* l);
void* L_reverseAcc(void* l, void* acc);
int32_t L_indexOf(void* l, int32_t i);
void* L_range(int32_t from, int32_t to);
int32_t L_sum(void* l);
void* L_concat(void* l1, void* l2);
int32_t L_contains(void* l, int32_t elem);
void* L_merge(void* l1, void* l2);
void* L_split(void* l);
void* L_mergeSort(void* l);
char* L_toString(void* l);
char* L_toString1(void* l);
void* L_take(void* l, int32_t n);
int32_t O_isfnined(void* o);
int32_t O_get(void* o);
int32_t O_getOrElse(void* o, int32_t i);
void* O_orElse(void* o1, void* o2);
void* O_toList(void* o);
char* Std_intToString(int32_t i);
char* Std_booleanToString(int32_t b);
int32_t Std_printBoolean(int32_t b);
void* MovingAverage_movingAverage(void* list, int32_t sum, int32_t count);

int32_t L_isEmpty(void* l) {
  int32_t match_0;
  if((*(int32_t*)l) == 0) {
    match_0 = 1;
  }
  else {
    if(1) {
      match_0 = 0;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_0;
}

int32_t L_length(void* l) {
  int32_t match_1;
  if((*(int32_t*)l) == 0) {
    match_1 = 0;
  }
  else {
    if((*(int32_t*)l) == 1 && 1 && 1) {
      void* t_0 = ((struct L_Cons*)l)->att1;
      match_1 = (1 + L_length(t_0));
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_1;
}

int32_t L_head(void* l) {
  int32_t match_2;
  if((*(int32_t*)l) == 1 && 1 && 1) {
    int32_t h_0 = ((struct L_Cons*)l)->att0;
    match_2 = h_0;
  }
  else {
    if((*(int32_t*)l) == 0) {
      char* string_0 = malloc(strlen("head(Nil)") + 1);
      strcpy(string_0, "head(Nil)");
      match_2 = Error_error(string_0);
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_2;
}

void* L_headOption(void* l) {
  void* match_3;
  if((*(int32_t*)l) == 1 && 1 && 1) {
    int32_t h_1 = ((struct L_Cons*)l)->att0;
    struct O_Some* struct_0 = malloc(sizeof(struct O_Some));
    struct_0->constr_index = 1;
    struct_0->att0 = h_1;
    match_3 = struct_0;
  }
  else {
    if((*(int32_t*)l) == 0) {
      struct O_None* struct_1 = malloc(sizeof(struct O_None));
      struct_1->constr_index = 0;
      match_3 = struct_1;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_3;
}

void* L_reverse(void* l) {
  struct L_Nil* struct_2 = malloc(sizeof(struct L_Nil));
  struct_2->constr_index = 0;
  return L_reverseAcc(l, struct_2);
}

void* L_reverseAcc(void* l, void* acc) {
  void* match_4;
  if((*(int32_t*)l) == 0) {
    match_4 = acc;
  }
  else {
    if((*(int32_t*)l) == 1 && 1 && 1) {
      int32_t h_2 = ((struct L_Cons*)l)->att0;
      void* t_1 = ((struct L_Cons*)l)->att1;
      struct L_Cons* struct_3 = malloc(sizeof(struct L_Cons));
      struct_3->constr_index = 1;
      struct_3->att0 = h_2;
      struct_3->att1 = acc;
      match_4 = L_reverseAcc(t_1, struct_3);
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_4;
}

int32_t L_indexOf(void* l, int32_t i) {
  int32_t match_5;
  if((*(int32_t*)l) == 0) {
    match_5 = (-1);
  }
  else {
    if((*(int32_t*)l) == 1 && 1 && 1) {
      int32_t h_3 = ((struct L_Cons*)l)->att0;
      void* t_2 = ((struct L_Cons*)l)->att1;
      int32_t ite_1;
      if((h_3 == i)) {
        ite_1 = 0;
      }
      else {
        int32_t rec_0 = L_indexOf(t_2, i);
        int32_t ite_0;
        if((0 <= rec_0)) {
          ite_0 = (rec_0 + 1);
        }
        else {
          ite_0 = (-1);
        }
        ite_1 = ite_0;
      }
      match_5 = ite_1;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_5;
}

void* L_range(int32_t from, int32_t to) {
  void* ite_2;
  if((to < from)) {
    struct L_Nil* struct_4 = malloc(sizeof(struct L_Nil));
    struct_4->constr_index = 0;
    ite_2 = struct_4;
  }
  else {
    struct L_Cons* struct_5 = malloc(sizeof(struct L_Cons));
    struct_5->constr_index = 1;
    struct_5->att0 = from;
    struct_5->att1 = L_range((from + 1), to);
    ite_2 = struct_5;
  }
  return ite_2;
}

int32_t L_sum(void* l) {
  int32_t match_6;
  if((*(int32_t*)l) == 0) {
    match_6 = 0;
  }
  else {
    if((*(int32_t*)l) == 1 && 1 && 1) {
      int32_t h_4 = ((struct L_Cons*)l)->att0;
      void* t_3 = ((struct L_Cons*)l)->att1;
      match_6 = (h_4 + L_sum(t_3));
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_6;
}

void* L_concat(void* l1, void* l2) {
  void* match_7;
  if((*(int32_t*)l1) == 0) {
    match_7 = l2;
  }
  else {
    if((*(int32_t*)l1) == 1 && 1 && 1) {
      int32_t h_5 = ((struct L_Cons*)l1)->att0;
      void* t_4 = ((struct L_Cons*)l1)->att1;
      struct L_Cons* struct_6 = malloc(sizeof(struct L_Cons));
      struct_6->constr_index = 1;
      struct_6->att0 = h_5;
      struct_6->att1 = L_concat(t_4, l2);
      match_7 = struct_6;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_7;
}

int32_t L_contains(void* l, int32_t elem) {
  int32_t match_8;
  if((*(int32_t*)l) == 0) {
    match_8 = 0;
  }
  else {
    if((*(int32_t*)l) == 1 && 1 && 1) {
      int32_t h_6 = ((struct L_Cons*)l)->att0;
      void* t_5 = ((struct L_Cons*)l)->att1;
      match_8 = ((h_6 == elem) || L_contains(t_5, elem));
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_8;
}

void* L_merge(void* l1, void* l2) {
  void* match_9;
  if((*(int32_t*)l1) == 0) {
    match_9 = l2;
  }
  else {
    if((*(int32_t*)l1) == 1 && 1 && 1) {
      int32_t h1_0 = ((struct L_Cons*)l1)->att0;
      void* t1_0 = ((struct L_Cons*)l1)->att1;
      void* match_10;
      if((*(int32_t*)l2) == 0) {
        match_10 = l1;
      }
      else {
        if((*(int32_t*)l2) == 1 && 1 && 1) {
          int32_t h2_0 = ((struct L_Cons*)l2)->att0;
          void* t2_0 = ((struct L_Cons*)l2)->att1;
          void* ite_3;
          if((h1_0 <= h2_0)) {
            struct L_Cons* struct_7 = malloc(sizeof(struct L_Cons));
            struct_7->constr_index = 1;
            struct_7->att0 = h1_0;
            struct_7->att1 = L_merge(t1_0, l2);
            ite_3 = struct_7;
          }
          else {
            struct L_Cons* struct_8 = malloc(sizeof(struct L_Cons));
            struct_8->constr_index = 1;
            struct_8->att0 = h2_0;
            struct_8->att1 = L_merge(l1, t2_0);
            ite_3 = struct_8;
          }
          match_10 = ite_3;
        }
        else {
          Error_error("Match error!");
        }
      }
      match_9 = match_10;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_9;
}

void* L_split(void* l) {
  void* match_11;
  if((*(int32_t*)l) == 1 && 1 && (*(int32_t*)((struct L_Cons*)l)->att1) == 1 && 1 && 1) {
    int32_t h1_1 = ((struct L_Cons*)l)->att0;
    int32_t h2_1 = ((struct L_Cons*)((struct L_Cons*)l)->att1)->att0;
    void* t_6 = ((struct L_Cons*)((struct L_Cons*)l)->att1)->att1;
    void* rec_1 = L_split(t_6);
    void* match_12;
    if((*(int32_t*)rec_1) == 0 && 1 && 1) {
      void* rec1_0 = ((struct L_LP*)rec_1)->att0;
      void* rec2_0 = ((struct L_LP*)rec_1)->att1;
      struct L_Cons* struct_10 = malloc(sizeof(struct L_Cons));
      struct_10->constr_index = 1;
      struct_10->att0 = h1_1;
      struct_10->att1 = rec1_0;
      struct L_Cons* struct_11 = malloc(sizeof(struct L_Cons));
      struct_11->constr_index = 1;
      struct_11->att0 = h2_1;
      struct_11->att1 = rec2_0;
      struct L_LP* struct_9 = malloc(sizeof(struct L_LP));
      struct_9->constr_index = 0;
      struct_9->att0 = struct_10;
      struct_9->att1 = struct_11;
      match_12 = struct_9;
    }
    else {
      Error_error("Match error!");
    }
    match_11 = match_12;
  }
  else {
    if(1) {
      struct L_Nil* struct_13 = malloc(sizeof(struct L_Nil));
      struct_13->constr_index = 0;
      struct L_LP* struct_12 = malloc(sizeof(struct L_LP));
      struct_12->constr_index = 0;
      struct_12->att0 = l;
      struct_12->att1 = struct_13;
      match_11 = struct_12;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_11;
}

void* L_mergeSort(void* l) {
  void* match_13;
  if((*(int32_t*)l) == 0) {
    match_13 = l;
  }
  else {
    if((*(int32_t*)l) == 1 && 1 && (*(int32_t*)((struct L_Cons*)l)->att1) == 0) {
      int32_t h_7 = ((struct L_Cons*)l)->att0;
      match_13 = l;
    }
    else {
      if(1) {
        void* xs_0 = l;
        void* match_14;
        if((*(int32_t*)L_split(xs_0)) == 0 && 1 && 1) {
          void* l1_0 = ((struct L_LP*)L_split(xs_0))->att0;
          void* l2_0 = ((struct L_LP*)L_split(xs_0))->att1;
          match_14 = L_merge(L_mergeSort(l1_0), L_mergeSort(l2_0));
        }
        else {
          Error_error("Match error!");
        }
        match_13 = match_14;
      }
      else {
        Error_error("Match error!");
      }
    }
  }
  return match_13;
}

char* L_toString(void* l) {
  char* match_15;
  if((*(int32_t*)l) == 0) {
    char* string_1 = malloc(strlen("List()") + 1);
    strcpy(string_1, "List()");
    match_15 = string_1;
  }
  else {
    if(1) {
      void* more_0 = l;
      char* string_4 = malloc(strlen("List(") + 1);
      strcpy(string_4, "List(");
      char* left_1 = string_4;
      char* right_1 = L_toString1(more_0);
      char* string_3 = malloc(strlen(left_1) + strlen(right_1) + 1);
      strcpy(string_3, left_1);
      strcat(string_3, right_1);
      char* string_5 = malloc(strlen(")") + 1);
      strcpy(string_5, ")");
      char* left_0 = string_3;
      char* right_0 = string_5;
      char* string_2 = malloc(strlen(left_0) + strlen(right_0) + 1);
      strcpy(string_2, left_0);
      strcat(string_2, right_0);
      match_15 = string_2;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_15;
}

char* L_toString1(void* l) {
  char* match_16;
  if((*(int32_t*)l) == 1 && 1 && (*(int32_t*)((struct L_Cons*)l)->att1) == 0) {
    int32_t h_8 = ((struct L_Cons*)l)->att0;
    match_16 = Std_intToString(h_8);
  }
  else {
    if((*(int32_t*)l) == 1 && 1 && 1) {
      int32_t h_9 = ((struct L_Cons*)l)->att0;
      void* t_7 = ((struct L_Cons*)l)->att1;
      char* string_8 = malloc(strlen(", ") + 1);
      strcpy(string_8, ", ");
      char* left_3 = Std_intToString(h_9);
      char* right_3 = string_8;
      char* string_7 = malloc(strlen(left_3) + strlen(right_3) + 1);
      strcpy(string_7, left_3);
      strcat(string_7, right_3);
      char* left_2 = string_7;
      char* right_2 = L_toString1(t_7);
      char* string_6 = malloc(strlen(left_2) + strlen(right_2) + 1);
      strcpy(string_6, left_2);
      strcat(string_6, right_2);
      match_16 = string_6;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_16;
}

void* L_take(void* l, int32_t n) {
  void* ite_4;
  if((n <= 0)) {
    struct L_Nil* struct_14 = malloc(sizeof(struct L_Nil));
    struct_14->constr_index = 0;
    ite_4 = struct_14;
  }
  else {
    void* match_17;
    if((*(int32_t*)l) == 0) {
      struct L_Nil* struct_15 = malloc(sizeof(struct L_Nil));
      struct_15->constr_index = 0;
      match_17 = struct_15;
    }
    else {
      if((*(int32_t*)l) == 1 && 1 && 1) {
        int32_t h_10 = ((struct L_Cons*)l)->att0;
        void* t_8 = ((struct L_Cons*)l)->att1;
        struct L_Cons* struct_16 = malloc(sizeof(struct L_Cons));
        struct_16->constr_index = 1;
        struct_16->att0 = h_10;
        struct_16->att1 = L_take(t_8, (n - 1));
        match_17 = struct_16;
      }
      else {
        Error_error("Match error!");
      }
    }
    ite_4 = match_17;
  }
  return ite_4;
}

int32_t O_isfnined(void* o) {
  int32_t match_18;
  if((*(int32_t*)o) == 0) {
    match_18 = 0;
  }
  else {
    if(1) {
      match_18 = 1;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_18;
}

int32_t O_get(void* o) {
  int32_t match_19;
  if((*(int32_t*)o) == 1 && 1) {
    int32_t i_0 = ((struct O_Some*)o)->att0;
    match_19 = i_0;
  }
  else {
    if((*(int32_t*)o) == 0) {
      char* string_9 = malloc(strlen("get(None)") + 1);
      strcpy(string_9, "get(None)");
      match_19 = Error_error(string_9);
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_19;
}

int32_t O_getOrElse(void* o, int32_t i) {
  int32_t match_20;
  if((*(int32_t*)o) == 0) {
    match_20 = i;
  }
  else {
    if((*(int32_t*)o) == 1 && 1) {
      int32_t oo_0 = ((struct O_Some*)o)->att0;
      match_20 = oo_0;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_20;
}

void* O_orElse(void* o1, void* o2) {
  void* match_21;
  if((*(int32_t*)o1) == 1 && 1) {
    match_21 = o1;
  }
  else {
    if((*(int32_t*)o1) == 0) {
      match_21 = o2;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_21;
}

void* O_toList(void* o) {
  void* match_22;
  if((*(int32_t*)o) == 1 && 1) {
    int32_t i_1 = ((struct O_Some*)o)->att0;
    struct L_Nil* struct_18 = malloc(sizeof(struct L_Nil));
    struct_18->constr_index = 0;
    struct L_Cons* struct_17 = malloc(sizeof(struct L_Cons));
    struct_17->constr_index = 1;
    struct_17->att0 = i_1;
    struct_17->att1 = struct_18;
    match_22 = struct_17;
  }
  else {
    if((*(int32_t*)o) == 0) {
      struct L_Nil* struct_19 = malloc(sizeof(struct L_Nil));
      struct_19->constr_index = 0;
      match_22 = struct_19;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_22;
}

char* Std_intToString(int32_t i) {
  char* ite_6;
  if((i < 0)) {
    char* string_11 = malloc(strlen("-") + 1);
    strcpy(string_11, "-");
    char* left_4 = string_11;
    char* right_4 = Std_intToString((-i));
    char* string_10 = malloc(strlen(left_4) + strlen(right_4) + 1);
    strcpy(string_10, left_4);
    strcat(string_10, right_4);
    ite_6 = string_10;
  }
  else {
    int32_t rem_0 = (i % 10);
    int32_t div_0 = (i / 10);
    char* ite_5;
    if((div_0 == 0)) {
      ite_5 = Std_digitToString(rem_0);
    }
    else {
      char* left_5 = Std_intToString(div_0);
      char* right_5 = Std_digitToString(rem_0);
      char* string_12 = malloc(strlen(left_5) + strlen(right_5) + 1);
      strcpy(string_12, left_5);
      strcat(string_12, right_5);
      ite_5 = string_12;
    }
    ite_6 = ite_5;
  }
  return ite_6;
}

char* Std_booleanToString(int32_t b) {
  char* ite_7;
  if(b) {
    char* string_13 = malloc(strlen("true") + 1);
    strcpy(string_13, "true");
    ite_7 = string_13;
  }
  else {
    char* string_14 = malloc(strlen("false") + 1);
    strcpy(string_14, "false");
    ite_7 = string_14;
  }
  return ite_7;
}

int32_t Std_printBoolean(int32_t b) {
  return Std_printString(Std_booleanToString(b));
}

void* MovingAverage_movingAverage(void* list, int32_t sum, int32_t count) {
  void* match_23;
  if((*(int32_t*)list) == 0) {
    struct L_Nil* struct_20 = malloc(sizeof(struct L_Nil));
    struct_20->constr_index = 0;
    match_23 = struct_20;
  }
  else {
    if((*(int32_t*)list) == 1 && 1 && 1) {
      int32_t h_11 = ((struct L_Cons*)list)->att0;
      void* t_9 = ((struct L_Cons*)list)->att1;
      int32_t average_0 = ((h_11 + sum) / (count + 1));
      struct L_Cons* struct_21 = malloc(sizeof(struct L_Cons));
      struct_21->constr_index = 1;
      struct_21->att0 = average_0;
      struct_21->att1 = MovingAverage_movingAverage(t_9, (sum + h_11), (count + 1));
      match_23 = struct_21;
    }
    else {
      Error_error("Match error!");
    }
  }
  return match_23;
}

int main() {
  struct L_Nil* struct_28 = malloc(sizeof(struct L_Nil));
  struct_28->constr_index = 0;
  struct L_Cons* struct_27 = malloc(sizeof(struct L_Cons));
  struct_27->constr_index = 1;
  struct_27->att0 = 6;
  struct_27->att1 = struct_28;
  struct L_Cons* struct_26 = malloc(sizeof(struct L_Cons));
  struct_26->constr_index = 1;
  struct_26->att0 = 1;
  struct_26->att1 = struct_27;
  struct L_Cons* struct_25 = malloc(sizeof(struct L_Cons));
  struct_25->constr_index = 1;
  struct_25->att0 = 4;
  struct_25->att1 = struct_26;
  struct L_Cons* struct_24 = malloc(sizeof(struct L_Cons));
  struct_24->constr_index = 1;
  struct_24->att0 = 7;
  struct_24->att1 = struct_25;
  struct L_Cons* struct_23 = malloc(sizeof(struct L_Cons));
  struct_23->constr_index = 1;
  struct_23->att0 = 2;
  struct_23->att1 = struct_24;
  struct L_Cons* struct_22 = malloc(sizeof(struct L_Cons));
  struct_22->constr_index = 1;
  struct_22->att0 = 5;
  struct_22->att1 = struct_23;
  void* list_0 = struct_22;
  Std_printString(L_toString(L_mergeSort(list_0)));
  Std_printString(L_toString(L_reverse(list_0)));
  return Std_printString(L_toString(MovingAverage_movingAverage(list_0, 0, 0)));
}