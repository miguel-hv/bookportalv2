export type Book = {
  id: number;
  title: string;
  author:string;
  review: string;
};

export type AddBookRequest = {
  title: string;
  author:string;
  review: string;
};

export type EditBookRequest = {
  title?: string;
  author?:string;
  review?: string;
};